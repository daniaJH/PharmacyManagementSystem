package com.mycompany.pharmacymanagementsystem;

public class Product {

    // =========================================================
    // BASIC PRODUCT INFORMATION
    // =========================================================

    private String code;
    private String tradeName;
    private String unit;
    private int minStockLevel;
    private String company;
    private double price;


    // =========================================================
    // MEDICINE INFORMATION
    // =========================================================

    private String dosage;
    private String scientificName;
    private String activeIngredients;
    private boolean prescriptionRequired;


    // =========================================================
    // CARE PRODUCT INFORMATION
    // =========================================================

    private String productType;
    private String usageMethod;


    // =========================================================
    // EMPTY CONSTRUCTOR
    // =========================================================

    public Product() {
    }


    // =========================================================
    // FULL BASIC CONSTRUCTOR
    // =========================================================

    public Product(
            String code,
            String tradeName,
            String unit,
            int minStockLevel,
            String company,
            double price) {

        this.code = code;
        this.tradeName = tradeName;
        this.unit = unit;
        this.minStockLevel = minStockLevel;
        this.company = company;
        this.price = price;
    }


    // =========================================================
    // PRODUCT CODE
    // =========================================================

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    // =========================================================
    // TRADE NAME
    // =========================================================

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }


    // =========================================================
    // UNIT
    // =========================================================

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    // =========================================================
    // MINIMUM STOCK
    // =========================================================

    public int getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(int minStockLevel) {
        this.minStockLevel = minStockLevel;
    }


    // These two methods are aliases used by ProductDAO

    public int getMinStock() {
        return minStockLevel;
    }

    public void setMinStock(int minStock) {
        this.minStockLevel = minStock;
    }


    // =========================================================
    // COMPANY / MANUFACTURER
    // =========================================================

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }


    // =========================================================
    // PRICE
    // =========================================================

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    // =========================================================
    // MEDICINE - DOSAGE
    // =========================================================

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }


    // =========================================================
    // MEDICINE - SCIENTIFIC NAME
    // =========================================================

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }


    // =========================================================
    // MEDICINE - ACTIVE INGREDIENTS
    // =========================================================

    public String getActiveIngredients() {
        return activeIngredients;
    }

    public void setActiveIngredients(String activeIngredients) {
        this.activeIngredients = activeIngredients;
    }


    // =========================================================
    // MEDICINE - PRESCRIPTION REQUIRED
    // =========================================================

    public boolean isPrescriptionRequired() {
        return prescriptionRequired;
    }

    public void setPrescriptionRequired(
            boolean prescriptionRequired) {

        this.prescriptionRequired = prescriptionRequired;
    }


    // =========================================================
    // CARE PRODUCT - PRODUCT TYPE
    // =========================================================

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }


    // These two methods are aliases used by ProductDAO

    public String getCareType() {
        return productType;
    }

    public void setCareType(String careType) {
        this.productType = careType;
    }


    // =========================================================
    // CARE PRODUCT - USAGE METHOD
    // =========================================================

    public String getUsageMethod() {
        return usageMethod;
    }

    public void setUsageMethod(String usageMethod) {
        this.usageMethod = usageMethod;
    }
}