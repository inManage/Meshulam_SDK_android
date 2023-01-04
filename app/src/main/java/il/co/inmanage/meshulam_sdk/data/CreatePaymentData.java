package il.co.inmanage.meshulam_sdk.data;

import java.util.List;

public class CreatePaymentData {

    private String userId;

    private String pageCode;

    private String apiKey;

    private String sum;

    private String description;

    private String fullName;

    private String phone;

    private String email;

    private String chargeType;

    private String applicationToken;

    private String templateType;

//    private List<String> cFieldsList;

    public String getUserId() {
        return userId;
    }

    public String getPageCode() {
        return pageCode;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSum() {
        return sum;
    }

    public String getDescription() {
        return description;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getChargeType() {
        return chargeType;
    }

    public String getApplicationToken() {
        return applicationToken;
    }

    public String getTemplateType() {
        return templateType;
    }

//    public List<String> getcFieldsList() {
//        return cFieldsList;
//    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public void setApplicationToken(String applicationToken) {
        this.applicationToken = applicationToken;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setPageCode(String pageCode) {
        this.pageCode = pageCode;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CreatePaymentData(Builder builder) {
        userId = builder.userId;
        pageCode = builder.pageCode;
        apiKey = builder.apiKey;
        sum = builder.sum;
        description = builder.description;
        fullName = builder.fullName;
        phone = builder.phone;
        email = builder.email;
        chargeType = builder.chargeType;
        applicationToken = builder.applicationToken;
        templateType = builder.templateType;
//        cFieldsList = builder.cFieldsList;
    }

    public static class Builder {

        private String userId;

        private String pageCode;

        private String apiKey;

        private String sum;

        private String description;

        private String fullName;

        private String phone;

        private String email;

        private String chargeType;

        private String applicationToken;

        private String templateType;

        private List<String> cFieldsList;

        public Builder setUserId(String userId){
            this.userId = userId;
            return this;
        }

        public Builder setPageCode(String pageCode){
            this.pageCode = pageCode;
            return this;
        }

        public Builder setApiKey(String apiKey){
            this.apiKey = apiKey;
            return this;
        }

        public Builder setSum(String sum){
            this.sum = sum;
            return this;
        }

        public Builder setDescription(String description){
            this.description = description;
            return this;
        }

        public Builder setFullName(String fullName){
            this.fullName = fullName;
            return this;
        }

        public Builder setPhone(String phone){
            this.phone = phone;
            return this;
        }

        public Builder setEmail(String email){
            this.email = email;
            return this;
        }

        public Builder setChargeType(String chargeType){
            this.chargeType = chargeType;
            return this;
        }
        public Builder setApplicationToken(String applicationToken){
            this.applicationToken = applicationToken;
            return this;
        }

        public Builder setTemplateType(String templateType){
            this.templateType = templateType;
            return this;
        }
//
//        public Builder setCFields(String ... strings){
//            this.cFieldsList = new ArrayList<>(Arrays.asList(strings));
//            return this;
//        }

        public CreatePaymentData create() {
            return new CreatePaymentData(this);
        }

    }
}
