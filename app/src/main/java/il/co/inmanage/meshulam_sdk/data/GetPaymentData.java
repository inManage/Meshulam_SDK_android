package il.co.inmanage.meshulam_sdk.data;

public class GetPaymentData {


    private String processId;
    private String pageCode;
    private String processToken;
    private String userId;
    private String apiKey;
    private String transactionId;
    private String sum;

    public String getProcessId() {
        return processId;
    }

    public String getPageCode() {
        return pageCode;
    }

    public String getProcessToken() {
        return processToken;
    }

    public String getUserId() { return userId; }

    public String getApiKey() { return apiKey; }

    public String getTransactionId() { return transactionId; }

    public String getSum() { return sum; }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public void setPageCode(String pageCode) {
        this.pageCode = pageCode;
    }

    public void setProcessToken(String processToken) {
        this.processToken = processToken;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public GetPaymentData(Builder builder) {
        processId = builder.processId;
        pageCode = builder.pageCode;
        processToken = builder.processToken;
        userId = builder.userId;
        apiKey = builder.apiKey;
        transactionId = builder.transactionId;
        sum = builder.sum;
    }

    public static class Builder {

        private String processId;
        private String pageCode;
        private String processToken;
        private String userId;
        private String apiKey;
        private String transactionId;
        private String sum;

        public Builder setProcessId(String processId){
            this.processId = processId;
            return this;
        }

        public Builder setPageCode(String pageCode){
            this.pageCode = pageCode;
            return this;
        }

        public Builder setProcessToken(String processToken){
            this.processToken = processToken;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder setTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder setSum(String sum) {
            this.sum = sum;
            return this;
        }

        public GetPaymentData create() {
            return new GetPaymentData(this);
        }

    }
}
