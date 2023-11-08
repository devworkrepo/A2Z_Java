package com.a2zsuvidhaa.`in`.util.pdf

import com.a2zsuvidhaa.`in`.data.model.TransactionDetail
import com.a2zsuvidhaa.`in`.util.NumberToWordConverter

fun dmtHtmlDataAsString(data: TransactionDetail,commission : String?): String {


    val declaimersBuilder = java.lang.StringBuilder()
    data.declaimers?.forEach {
        val value =  "<p style=\"margin: 0 0 0px !important; font-size: 13px\">${it}</p>\n" + ""
        declaimersBuilder.append(value)
    }


    val strTransactionDetailsBuilder = StringBuilder()

    data.dmtDetails?.let {
        for (i in 0 until it.size) {
            val value = """
             <tr style="border: 1px solid #888;">
                <td><span></span>${it[i].txnTime}</td>

                <td><span >${it[i].txnId}</span></td>
                <td><span >${it[i].bankRef}</span></td>
                <td><span >${it[i].amount}</span></td>
                <td><span >${it[i].statusDesc}</span></td>
            </tr>
        """.trimIndent()
            strTransactionDetailsBuilder.append(value)
        }
    }

    val totalAmount = data.totalAmount?.run {
        if(commission!=null){
            (this.toDouble() + commission.toDouble())
        }
        else this.toDouble()
    } ?: 0.0


    val pdfString = """
        <div id="myReciept" class="modal fade" role="dialog" data-backdrop="static" data-keyboard="false">
            <div class="modal-dialog" style="width: 900px; margin: 30px auto;">
                <div class="modal-content">
                    <div class="modal-body" style="padding: 10px; border-radius: 0px;">
                        <div class="col-md-12">
                            <button type="button" class="btn" data-dismiss="modal" style="padding: 6px ! important; top: -8px; right: -35px; background-color: rgb(255, 255, 255) ! important; position: absolute;">&times;</button>
                        </div>
                        <div class="containers" style="overflow: auto; width: 100%">
                            <div class="panel panel-primary">
                               
                                <div class="panel-body" style="padding: 0% 4%">
                                    <div class="clearfix"></div>
                                    <div class="row">
                                        <div class="col-md-9"></div>
                                        <div class="col-md-3">
                                           
                                        </div>
                                    </div>
                                    <div class="clearfix"></div>
                                    <div id="reciept" style="">

                                        <div class="row">
                                            <div class="col-md-12" id="dvContents">
                                                
                                                 <table style="width: 100%;font-size: 15px;border: 1px solid black; border-collapse: collapse;">
                                                    <tr>
                                                        <td> <img src="https://partners.a2zsuvidhaa.com/newlog/images/Logo168.png" style="width:70px;" /></td>
                                                        <td style="text-align:center"> <b>Outlet Name:${data.outletName}</b><br>Address: ${data.outletAddress}<br>Contact Number: ${data.outletNumber}</td>
                                                        
                                                 </table>
                                                <table style="width: 100%;font-size: 15px;border: 1px solid black; border-collapse: collapse;" >
                                                    
                                                    <tr style="" id="trandetailbybps">
                                                        <td>
                                                            <b class="cs" > Account No :</b><span >${data.number}</span><br />
                                                            <b class="cs" > Bene Name :</b><span >${data.name}</span><br />
                                                            <b class="cs" >  Bank Name :</b><span >${data.bankName}</span><br />
                                                           
                                                        </td>
                                                        <td>
                                                         
                                                            <b class="cs" > IFSC Code :</b><span >${data.ifsc}</span><br />
                                                            <b class="cs" > Transaction Type :</b>
                                                            <span>${data.paymentChannel}</span><br/>
                                                             <b class="cs" > Wallet Name :</b>
                                                            <span>${data.serviceName}</span><br/>
        													
                                                        </td>
                                                        <td> <b class="cs" > Sender Number :</b>
                                                            <span>${data.senderNumber}</span><br/>
                                                            <b class="cs" > Sender Name :</b>
                                                            <span>${data.senderName}</span><br/></td>
                                                    </tr>
        											<tr>
                                                        <td colspan="3" style="border-bottom: 1px solid #ccc; border-top: 1px solid #ccc;"><b>Transaction Details&nbsp;&nbsp;&nbsp;&nbsp;       </b><span id="bbps_id"></span></td>
                                                        
                                                    </tr>
                                                    <tr>
                                                        <td colspan="3" class="nospace1">
                                                            <table class="table table-bordered" style="width:100%;font-size:15px;border: 1px solid black;" >
        														<thead>
        				<tr style="background:#ddd;border: 1px solid black;">
        				<td class="phead" style=""><b>Date</b></td>
        				<td class="phead" style=""><b>Transaction ID </b></td>
        				<td class="phead" style=""><b>Bank Ref </b></td>
        				<td class="phead" style=""><b>Amount </b></td>
        				<td class="phead" style=""><b>Status </b></td>
        				</tr>
                                                                </thead>
                                                                <tbody id="printTBody">
                                                                                    ${strTransactionDetailsBuilder.toString()}
                                                                </tbody>
                                                            </table>
                                                        </td>
                                                    </tr>
                                                  <tr>
                                                        <td colspan="3" style="padding-top: 5px;" >                                                   
                                                            <div class="cs col-md-12" style="font-size:13px">
                                                               
                                                               <b>Total Amount Rs. : </b>
                                                         <label id="printAmount">${totalAmount}</label>&nbsp;&nbsp;&nbsp;(Inc. of GST)
                                                        <br>
                                                       
                                                                    <b>Amount in Words :${NumberToWordConverter.doubleConvert(totalAmount)}</b>
                                                                  
                                                             
                                                            </div>
                                                        </td>
                                                    </tr>  
                                                     <tr>
                                                           <td colspan="3" style="padding: 0px 8px;" >                
                                                            <b>Disclaimer :</b><br>
                                                            
                                                               ${declaimersBuilder.toString()}
                                                            
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="3" class="modalfoot" style="text-align: center; padding: 0px;" >
                                                            <p style="margin: 0 0 0px !important;" >&copy; 2018 All Rights Reserved</p>
                                                          
                                                        </td>
                                                    </tr>
                                                 
                                                </table> 
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    """.trimIndent()
    return pdfString
}