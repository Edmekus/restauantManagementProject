package com.cafe_backend.ServiceImple;

import com.cafe_backend.Constants.RestaurantContants;
import com.cafe_backend.Dao.BillInterface;
import com.cafe_backend.JWT.JwtFilter;
import com.cafe_backend.Models.Bill;
import com.cafe_backend.Service.BillService;
import com.cafe_backend.Util.RestaurantUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.persistence.Column;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BillServiceImple implements BillService{

    // Assurez-vous que vous avez un ObjectMapper configuré
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    BillInterface billInterface;


    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("À l'intérieur de generateReport");
        try {
            String fileName;
            if (validateRequestMap(requestMap)) {
                if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
                    fileName = (String) requestMap.get("uuid");
                } else {
                    fileName = RestaurantUtils.getUUID();
                    requestMap.put("uuid", fileName);
                    insertBill(requestMap);
                }

                String data = "Nom: " + requestMap.get("name") + "\n" +
                        "Numéro de contact: " + requestMap.get("contactNumber") + "\n" +
                        "Email: " + requestMap.get("email") + "\n" +
                        "Mode de paiement: " + requestMap.get("paymentMethod");

                try (FileOutputStream fos = new FileOutputStream(RestaurantContants.store_location + "\\" + fileName + ".pdf")) {
                    Document document = new Document();
                    PdfWriter.getInstance(document, fos);
                    document.open();
                    setRectangleInPdf(document);

                    Paragraph chunk = new Paragraph("Système de Gestion de Restaurant", getFont("Header"));
                    chunk.setAlignment(Element.ALIGN_CENTER);
                    document.add(chunk);

                    Paragraph paragraph = new Paragraph(data + "\n\n", getFont("Data"));
                    document.add(paragraph);

                    PdfPTable table = new PdfPTable(5);
                    table.setWidthPercentage(100);
                    addTableHeader(table);

                    // Récupération de productDetails de la requête
                    Object productDetails = requestMap.get("productDetails");

                    JSONArray jsonArray;

                    if (productDetails instanceof String) {
                        // Si productDetails est déjà un String, on le convertit en JSONArray
                        jsonArray = RestaurantUtils.getJsonArrayFromString((String) productDetails);
                    } else if (productDetails instanceof ArrayList) {
                        // Si productDetails est une ArrayList, on le convertit en JSONArray directement
                        jsonArray = new JSONArray((ArrayList<?>) productDetails);
                    } else {
                        // Gestion d'erreur si le type est inattendu
                        throw new IllegalArgumentException("productDetails doit être de type String ou ArrayList");
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        addRows(table, RestaurantUtils.getMapFromJson(jsonArray.getString(i)));
                    }
                    document.add(table);

                    Paragraph footer = new Paragraph("Total: " + requestMap.get("totalAmount") + "\n" +
                            "Merci de votre visite. S'il vous plaît, visitez à nouveau !!", getFont("Data"));
                    document.add(footer);
                    document.close();
                }

                return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);
            }
            return RestaurantUtils.getResponseEntity("Données requises introuvables", HttpStatus.BAD_REQUEST);
        } catch (Exception g) {
            log.error("Erreur lors de l'insertion de la facture: ", g);
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void addRows(PdfPTable table, Map<String ,Object> data) {
        log.info("Inside addRows");
        table.addCell((String) data.get("name"));
        table.addCell((String) data.get("category"));
        table.addCell((String) data.get("quantity"));
        table.addCell(Double.toString((Double) data.get("price")));
        table.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable table) {
        log.info("Inside addTableHeader");
        String[] columnTitles = {"Nom", "Catégorie", "Quantité", "Prix", "Total"};

        for (String columnTitle : columnTitles) {
            PdfPCell header = new PdfPCell();
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(columnTitle));
            header.setBackgroundColor(BaseColor.CYAN);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setVerticalAlignment(Element.ALIGN_CENTER);
            table.addCell(header);
        }
    }


    private Font getFont(String type) {
        log.info("Inside getFont");
        switch (type){
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;

            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;

            default:
                return new Font();

        }
    }

    private void setRectangleInPdf(Document document) throws DocumentException {
        log.info("Al'intéreur de setRectangleInPdf ");
        Rectangle rect = new Rectangle(577,852,15,18);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);
        document.add(rect);
    }

    // Ajoute cette méthode dans ta classe BillServiceImple
    private boolean isValidJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    // Modifie la méthode insertBill pour inclure la validation JSON
    private void insertBill(Map<String, Object> requestMap) {
        try {
            Bill bill = new Bill();

            // Validation et récupération des valeurs
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));

            // Conversion de total avec gestion d'erreur
            Object totalAmountObj = requestMap.get("totalAmount");
            if (totalAmountObj == null) {
                log.error("TotalAmount ne peut pas être nul");
                throw new IllegalArgumentException("TotalAmount ne peut pas être nul");
            }

            int totalAmount;
            if (totalAmountObj instanceof Number) {
                totalAmount = ((Number) totalAmountObj).intValue();
            } else {
                log.error("Format du totalAmount invalide: " + totalAmountObj.getClass().getName());
                throw new IllegalArgumentException("Format du totalAmount invalide");
            }
            bill.setTotal(totalAmount);

            // Récupération et validation de productDetails
            Object productDetailsObj = requestMap.get("productDetails");

            if (productDetailsObj instanceof String) {
                // Si productDetailsObj est une String, cast et utilisation normale
                String productDetails = (String) productDetailsObj;

                // Validation JSON
                if (isValidJson(productDetails)) {
                    bill.setProductDetails(productDetails);
                } else {
                    log.error("ProductDetails JSON invalide.");
                    throw new IllegalArgumentException("ProductDetails JSON invalide.");
                }
            } else if (productDetailsObj instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<?> productDetailsList = (List<?>) productDetailsObj;

                if (productDetailsList != null && !productDetailsList.isEmpty()) {
                    // Convertir la liste en JSON
                    String productDetailsJson = objectMapper.writeValueAsString(productDetailsList);
                    bill.setProductDetails(productDetailsJson);
                } else {
                    log.warn("productDetailsList est nul ou vide.");
                    bill.setProductDetails("[]");
                }
            }

            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billInterface.save(bill);

            log.info("Facture insérée avec succès.");

        } catch (JsonProcessingException e) {
            log.error("Erreur lors de la conversion JSON pour productDetails: ", e);
        } catch (Exception e) {
            log.error("Erreur lors de l'insertion de la facture: ", e);
        }
    }


    private boolean validateRequestMap(Map<String, Object> requestMap) {
       return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        List <Bill> list = new ArrayList<>();
        if (jwtFilter.isAdmin()){
            list = billInterface.getAllBills();
        }else {
            list = billInterface.getBillByUserName(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("Inside getPdf: requestMap {}",requestMap);
        try {
            byte[] byteArray = new byte[0];
            if (!requestMap.containsKey("uuid") && validateRequestMap(requestMap))
                return new ResponseEntity<>(byteArray,HttpStatus.BAD_REQUEST);
            String filePath = RestaurantContants.store_location+"\\"+(String) requestMap.get("uuid")+".pdf";
            if (RestaurantUtils.isFileExist(filePath)){
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray,HttpStatus.OK);
            }else {
                requestMap.put("isGenerate",false);
                generateReport(requestMap);
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray,HttpStatus.OK);
            }
        }catch (Exception r){
            r.printStackTrace();
        }
        return null;
    }

    private byte[] getByteArray(String filePath) throws Exception {

        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);
        byte[] byteArray = IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }

    @Override
    public ResponseEntity<String> deleteBill(Long id) {
        try {
            Optional optional = billInterface.findById(id);
            if (!optional.isEmpty()){
                billInterface.deleteById(id);
                return RestaurantUtils.getResponseEntity("La facture a été supprimée avec succès",HttpStatus.OK);
            }else {
                return RestaurantUtils.getResponseEntity("L'identifiant de la facture n'existe pas",HttpStatus.OK);
            }
        }catch (Exception j){
            j.printStackTrace();
        }
        return RestaurantUtils.getResponseEntity(RestaurantContants.error_message,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
