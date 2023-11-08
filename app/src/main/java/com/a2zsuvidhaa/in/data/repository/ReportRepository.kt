package com.a2zsuvidhaa.`in`.data.repository

import com.a2zsuvidhaa.`in`.data.service.ReportService
import com.a2zsuvidhaa.`in`.util.ents.FieldMapData
import javax.inject.Inject

class ReportRepository @Inject constructor(private val reportService: ReportService) {

    suspend fun ledgerReport(data: FieldMapData) = reportService.ledgerReport(data)
    suspend fun checkStatus(data: FieldMapData) = reportService.checkStatus(data)
    suspend fun makeComplain(data: FieldMapData) = reportService.makeComplain(data)
    suspend fun fetchComplainTypes(data: FieldMapData) = reportService.fetchComplainTypes(data)
    suspend fun downloadLedgerReceiptData(url: String) = reportService.downloadLedgerReceiptData(url)
    suspend fun schemeList() = reportService.schemeList()
    suspend fun schemeDetail(data : FieldMapData) = reportService.schemeDetail(data)

}