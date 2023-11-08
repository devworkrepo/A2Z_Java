package com.a2zsuvidhaa.`in`.data.repository

import com.a2zsuvidhaa.`in`.data.service.AepsService
import com.a2zsuvidhaa.`in`.data.service.AgreementService
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData
import javax.inject.Inject


class AgreementRepository @Inject constructor(private val agreementService: AgreementService) {


    suspend fun fetchInitialAgreement() = agreementService.fetchInitialAgreement()
    suspend fun fetchInitialIrctcAgreement() = agreementService.fetchInitialIrctcAgreement()

    suspend fun generateAgreementPdf() = agreementService.generateAgreementPdf()
    suspend fun generateIrctcAgreementPdf() = agreementService.generateIrctcPdf()


    suspend fun startAgreement() =
        agreementService.startAgreement(hashMapOf("undefine" to "undefine"))

    suspend fun startIrctcAgreement() =
        agreementService.startIrctcAgreement(hashMapOf("undefine" to "undefine"))


    suspend fun checkStatus(data: FieldMapData) = agreementService.checkStatus(data)
    suspend fun checkStatusIrctc(data: FieldMapData) = agreementService.checkStatusIrctc(data)


    suspend fun agreementDownload(data: FieldMapData) = agreementService.agreementDownload(data)
    suspend fun agreementIrctcDownload(data: FieldMapData) =
        agreementService.agreementIrctcDownload(data)


    suspend fun agreementDownloadReport(data: FieldMapData) =
        agreementService.agreementDownloadReport(data)

    suspend fun agreementDownloadReportIrctc(data: FieldMapData) =
        agreementService.agreementDownloadReportIrctc(data)


}