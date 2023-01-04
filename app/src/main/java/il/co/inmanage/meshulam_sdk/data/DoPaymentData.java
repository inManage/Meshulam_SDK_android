package il.co.inmanage.meshulam_sdk.data;

import il.co.inmanage.meshulam_sdk.parser.Parser;
import il.co.inmanage.meshulam_sdk.server_responses.BaseResponse;

import org.json.JSONObject;

public class DoPaymentData extends BaseResponse {

    private String fullName, phone, email, sum, description, transactionTypeId, paymentNum, typeId,
    pageHash, firstPaymentSum, periodicalPaymentSum, platform, invoiceName;
    private boolean isShowPaymentsSelect;

    public DoPaymentData() {
    }

    public DoPaymentData(String fullName, String phone, String email, String sum, String description, String transactionTypeId, String paymentNum, String typeId, String pageHash, String firstPaymentSum, String periodicalPaymentSum, String platform, boolean isShowPaymentsSelect, String invoiceName) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.sum = sum;
        this.description = description;
        this.transactionTypeId = transactionTypeId;
        this.paymentNum = paymentNum;
        this.typeId = typeId;
        this.pageHash = pageHash;
        this.firstPaymentSum = firstPaymentSum;
        this.periodicalPaymentSum = periodicalPaymentSum;
        this.platform = platform;
        this.invoiceName = invoiceName;
        this.isShowPaymentsSelect = isShowPaymentsSelect;
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

    public String getSum() {
        return sum;
    }

    public String getDescription() {
        return description;
    }

    public String getTransactionTypeId() {
        return transactionTypeId;
    }

    public String getPaymentNum() {
        return paymentNum;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getPageHash() {
        return pageHash;
    }

    public String getFirstPaymentSum() {
        return firstPaymentSum;
    }

    public String getPeriodicalPaymentSum() {
        return periodicalPaymentSum;
    }

    public String getPlatform() {
        return platform;
    }

    public boolean isShowPaymentsSelect() {
        return isShowPaymentsSelect;
    }

    public String getInvoiceName() {
        return invoiceName;
    }

    @Override
    public BaseResponse createResponse(JSONObject jsonObject) {
        DoPaymentData doPaymentData = new DoPaymentData();
        doPaymentData.fullName = Parser.jsonParse(jsonObject, "full_name", Parser.createTempString());
        doPaymentData.phone = Parser.jsonParse(jsonObject, "phone", Parser.createTempString());
        doPaymentData.email = Parser.jsonParse(jsonObject, "email", Parser.createTempString());
        doPaymentData.invoiceName = Parser.jsonParse(jsonObject, "invoiceName", Parser.createTempString());
        doPaymentData.sum = Parser.jsonParse(jsonObject, "sum", Parser.createTempString());
        doPaymentData.description = Parser.jsonParse(jsonObject, "description", Parser.createTempString());
        doPaymentData.transactionTypeId = Parser.jsonParse(jsonObject, "transaction_type_id", Parser.createTempString());
        doPaymentData.paymentNum = Parser.jsonParse(jsonObject, "payment_num", Parser.createTempString());
        doPaymentData.typeId = Parser.jsonParse(jsonObject, "type_id", Parser.createTempString());
        doPaymentData.pageHash = Parser.jsonParse(jsonObject, "page_hash", Parser.createTempString());
        doPaymentData.firstPaymentSum = Parser.jsonParse(jsonObject, "first_payment_sum", Parser.createTempString());
        doPaymentData.periodicalPaymentSum = Parser.jsonParse(jsonObject, "periodical_payment_sum", Parser.createTempString());
        doPaymentData.platform = Parser.jsonParse(jsonObject, "platform", Parser.createTempString());
        doPaymentData.isShowPaymentsSelect = Parser.jsonParse(jsonObject, "show_payments_select",false);
        return doPaymentData;
    }
}
