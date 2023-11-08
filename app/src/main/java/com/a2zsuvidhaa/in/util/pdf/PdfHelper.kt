package com.a2zsuvidhaa.`in`.util.pdf

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.a2zsuvidhaa.`in`.data.model.TransactionDetailResponse
import com.a2zsuvidhaa.`in`.listener.WebApiCallListener
import com.a2zsuvidhaa.`in`.util.APIs
import com.a2zsuvidhaa.`in`.util.AppConstants
import com.a2zsuvidhaa.`in`.util.AppLog
import com.a2zsuvidhaa.`in`.util.WebApiCall
import com.a2zsuvidhaa.`in`.util.dialogs.StatusDialog
import com.a2zsuvidhaa.`in`.util.ents.showToast
import com.google.gson.Gson
import io.github.lucasfsc.html2pdf.Html2Pdf
import org.json.JSONObject
import java.io.File


object PdfHelper {


    fun downloadPdfData(
        context: Context,
        recordId: String,
        commission: String? = null,
        isAeps: Boolean = false,
        isMatm: Boolean = false,
    ) {

        val dialog = StatusDialog.progress(context)
        val url = if (isAeps) AppConstants.BASE_URL + "aeps/report/slip/new" + "/$recordId"
        else if (isMatm) AppConstants.BASE_URL + "matm/report-slip/" + recordId
        else APIs.DOWNLOAD_NEW_RECEIPT + "/$recordId"
        WebApiCall.getRequestWithHeader(context, url)
        WebApiCall.webApiCallback(object : WebApiCallListener {
            override fun onFailure(message: String?) {
                dialog.dismiss()
            }

            override fun onSuccessResponse(jsonObject: JSONObject) {
                dialog.dismiss()
                try {
                    onLedgerSuccessResponse(
                        context, Gson().fromJson(
                            jsonObject.toString(),
                            TransactionDetailResponse::class.java
                        ), commission, isAeps,isMatm
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }


    private fun onLedgerSuccessResponse(
        context: Context,
        data: TransactionDetailResponse,
        commission: String?,
        isAeps: Boolean,
        isMatm: Boolean,
    ) {

        data.data?.let { transactionDetail ->

            val pdfString = if (isAeps) {
                aepsHtmlDataAsString(transactionDetail)
            }
            else if(isMatm){
                matmHtmlDataAsString(transactionDetail)
            }
            else {
                when (transactionDetail.slipType) {
                    "UPI_PAYMENT" -> upiHtmlDataAsString(transactionDetail, commission)
                    "TRANSACTION" -> dmtHtmlDataAsString(transactionDetail, commission)
                    "BILLPAYMENT", "FASTTAG" -> bbpsHtmlDataAsString(transactionDetail)
                    "RECHARGE" -> rechargeHtmlDataAsString(transactionDetail)
                    "AEPS" -> aepsHtmlDataAsString(transactionDetail)
                    "MATM" -> matmHtmlDataAsString(transactionDetail)
                    else -> null
                }
            }



            pdfString?.let {
                generatePdfUriFromHtmlString(context, pdfString) {
                    viewPdf(it, context)
                }
            }
        }

    }


    private fun generatePdfUriFromHtmlString(
        context: Context,
        strPdfHtml: String,
        callback: (Uri?) -> Unit
    ) {

        context.cacheDir.deleteRecursively()
        val cachePath = File(context.externalCacheDir, "images/")
        cachePath.mkdirs()
        val file = File(cachePath, "${System.currentTimeMillis()}_a2z_receipt.pdf")

        val converter = Html2Pdf.Companion.Builder()
            .context(context)
            .html(strPdfHtml)
            .file(file)
            .build()

        //can be called with a callback to warn the user, share file...
        converter.convertToPdf(object : Html2Pdf.OnCompleteConversion {
            override fun onFailed() {
                context.showToast("Unable to download file")
                callback(null)
            }

            override fun onSuccess() {
                val photoURI: Uri = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName.toString() + ".provider", file
                )
                callback(photoURI)
            }
        })
        //or without a callback
        //converter.convertToPdf()

        /*PDFUtil.generatePDFFromHTML(context, file, strPdfHtml, object : PDFPrint.OnPDFPrintListener {
            override fun onSuccess(file: File) {

                try {
                    val photoURI: Uri = FileProvider.getUriForFile(context,
                            context.applicationContext.packageName.toString() + ".provider", file)
                    callback(photoURI)
                } catch (e: Exception) {
                    context.showToast("Application not found! ${e.message}")
                    callback(null)
                }
            }

            override fun onError(exception: java.lang.Exception) {
                exception.printStackTrace()
                context.showToast("something went wrong!")
                callback(null)
            }
        })*/
    }


    fun viewPdf(uri: Uri?, context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/pdf")
            context.startActivity(intent)
        } catch (e: Exception) {
            AppLog.d("file : exception : ${e.message}")
            context.showToast("Application not found! ${e.message}")
        }
    }
}

