package com.josalla.store.model;

public class Offers {
    private  String offer_id;
    private  String offer_image1 ;
    private  String offer_image2 ;
    private  String offer_image3 ;
    private  String offer_image4 ;
    private  String offer_image5 ;
    private  String prduct_Id1;
    private  String prduct_Id2;
    private  String prduct_Id3;
    private  String prduct_Id4;
    private  String prduct_Id5;

    public Offers(String offer_image1, String offer_image2, String offer_image3, String offer_image4, String offer_image5, String prduct_Id1, String prduct_Id2, String prduct_Id3, String prduct_Id4, String prduct_Id5) {
        this.offer_image1 = offer_image1;
        this.offer_image2 = offer_image2;
        this.offer_image3 = offer_image3;
        this.offer_image4 = offer_image4;
        this.offer_image5 = offer_image5;
        this.prduct_Id1 = prduct_Id1;
        this.prduct_Id2 = prduct_Id2;
        this.prduct_Id3 = prduct_Id3;
        this.prduct_Id4 = prduct_Id4;
        this.prduct_Id5 = prduct_Id5;
    }

    public Offers() {
    }

    public String getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(String offer_id) {
        this.offer_id = offer_id;
    }

    public String getOffer_image1() {
        return offer_image1;
    }

    public void setOffer_image1(String offer_image1) {
        this.offer_image1 = offer_image1;
    }

    public String getOffer_image2() {
        return offer_image2;
    }

    public void setOffer_image2(String offer_image2) {
        this.offer_image2 = offer_image2;
    }

    public String getOffer_image3() {
        return offer_image3;
    }

    public void setOffer_image3(String offer_image3) {
        this.offer_image3 = offer_image3;
    }

    public String getOffer_image4() {
        return offer_image4;
    }

    public void setOffer_image4(String offer_image4) {
        this.offer_image4 = offer_image4;
    }

    public String getOffer_image5() {
        return offer_image5;
    }

    public void setOffer_image5(String offer_image5) {
        this.offer_image5 = offer_image5;
    }

    public String getPrduct_Id1() {
        return prduct_Id1;
    }

    public void setPrduct_Id1(String prduct_Id1) {
        this.prduct_Id1 = prduct_Id1;
    }

    public String getPrduct_Id2() {
        return prduct_Id2;
    }

    public void setPrduct_Id2(String prduct_Id2) {
        this.prduct_Id2 = prduct_Id2;
    }

    public String getPrduct_Id3() {
        return prduct_Id3;
    }

    public void setPrduct_Id3(String prduct_Id3) {
        this.prduct_Id3 = prduct_Id3;
    }

    public String getPrduct_Id4() {
        return prduct_Id4;
    }

    public void setPrduct_Id4(String prduct_Id4) {
        this.prduct_Id4 = prduct_Id4;
    }

    public String getPrduct_Id5() {
        return prduct_Id5;
    }

    public void setPrduct_Id5(String prduct_Id5) {
        this.prduct_Id5 = prduct_Id5;
    }
}
