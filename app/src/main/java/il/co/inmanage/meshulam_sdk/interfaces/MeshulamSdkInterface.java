package il.co.inmanage.meshulam_sdk.interfaces;

import org.jetbrains.annotations.NotNull;

import il.co.inmanage.meshulam_sdk.data.CreatePaymentData;
import il.co.inmanage.meshulam_sdk.data.GetPaymentData;
import il.co.inmanage.meshulam_sdk.sdk.SdkManager;

public interface MeshulamSdkInterface {

    /**
     * @param STATUS_PAYMENT_SUCCESS
     * @return If the transaction was successful and awaits settleSuspendedTransaction
     */
    public final static int STATUS_PAYMENT_SUCCESS = 11;

    /**
     * This methods creates a payment process, it gets CreatePaymentData object that
     * contains all the relevant params
     *
     * @param createPaymentData has a builder, this are his params:
     *
     * pageCode (required) : is for your API settings at Meshulam.
     * There might be more than one pageCode, for instance,
     * a pageCode for credit card template only and a pageCode for Bit.
     *
     * apiKey (required) : refers to companies that are working with multiple businesses.
     * In this case, you will need to send both the apiKey and the userId for every transaction that you would like to clear.
     *
     * userId (required) : refers to every business that is connected to Meshulam.
     *
     * sum (required): the amount you want to charge the client with.
     *
     * fullName (required) : the full name of the client.
     *
     * phone (required) : the phone num of the user.
     *
     * email (optional) : the email of the user.
     *
     * description (optional) : description of the user.
     *
     * @param onPaymentResultListener has four callbacks :
     * onPaymentSuccess : returns a bundle that contains
     * processId, processToken, bitPaymentId and transactionId in order to getPaymentProcessInfo, cancelBitPayment
     * and settleSuspendedTransaction.
     *
     * onGetPaymentData : returns a bundle that has processId and processToken in order to getPaymentProcessInfo.
     *
     * onPaymentCanceled : if the user decides to cancel the payment process.
     */

    void createPaymentProcess(CreatePaymentData createPaymentData, @NotNull SdkManager.OnPaymentResultListener onPaymentResultListener);

    /**
     * This methods makes the payment J4 from J5, it gets GetPaymentData object that
     * contains all the relevant params
     *
     * @param getPaymentData has a builder, this are his params:
     *
     * transactionId (required) : returns from onPaymentSuccess of createPaymentProcess,
     * and getPaymentProcessInfo.
     *
     * apiKey (required) : refers to companies that are working with multiple businesses.
     * In this case, you will need to send both the apiKey and the userId for every transaction that you would like to clear.
     *
     * userId (required) : refers to every business that is connected to Meshulam.
     *
     * sum (required): the amount you want to charge the client with.
     *
     * @param onSettlePaymentListener:
     * onSettlePaymentSuccess : returns json string of settleSuspendedTransaction.
     */

    void settleSuspendedTransaction (GetPaymentData getPaymentData, @NotNull SdkManager.OnSettlePaymentListener onSettlePaymentListener);

    /**
     * This methods queries a payment process in order to get the status additional params regarding the payment ,
     * it gets GetPaymentData object that
     * contains all the relevant params
     *
     * @param getPaymentData has a builder, this are his params:
     *
     * pageCode (required) : is for your API settings at Meshulam.
     * There might be more than one pageCode, for instance,
     * a pageCode for credit card template only and a pageCode for Bit.
     *
     * processToken (required) : returns from onPaymentSuccess and onGetPaymentData of createPaymentProcess.
     *
     * processId (required) : returns from onPaymentSuccess and onGetPaymentData of createPaymentProcess.
     *
     * @param onGetInfoListener:
     * getPaymentInfoData : returns json string of getPaymentProcessInfo.
     */

    void getPaymentProcessInfo (GetPaymentData getPaymentData,@NotNull SdkManager.OnGetInfoListener onGetInfoListener);

    /**
     * This methods cancels a payment process that was successful but wasn't settled,
     * it gets GetPaymentData object that
     * contains all the relevant params
     *
     * @param getPaymentData has a builder, this are his params:
     *
     * bitPaymentId (required) : received from onPaymentSuccess of createPaymentProcess
     *
     * @param onPaymentResultListener:
     * onPaymentCanceled : called when the payment was successfully canceled.
     */

    void cancelBitPayment (GetPaymentData getPaymentData,@NotNull SdkManager.OnResultResultListener onPaymentResultListener);
}
