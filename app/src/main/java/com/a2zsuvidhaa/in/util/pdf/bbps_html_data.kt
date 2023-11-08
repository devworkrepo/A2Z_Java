package com.a2zsuvidhaa.`in`.util.pdf

import com.a2zsuvidhaa.`in`.data.model.TransactionDetail
import com.a2zsuvidhaa.`in`.util.NumberToWordConverter
import java.lang.StringBuilder

fun bbpsHtmlDataAsString(data : TransactionDetail) : String {
    val declaimersBuilder = StringBuilder()
    data.declaimers?.forEach {
        val value =  "<p style=\"margin: 0 0 0px !important; font-size: 13px\">${it}</p>\n" + ""
        declaimersBuilder.append(value)
    }

    return """
    <style>
        .modal-body {
            line-height: 21px;
        }
        .panel {
            color: #222 !important;
        }
        .modal-body p {
            line-height: 20px;
            margin-bottom: 0px;
        }
        .modalfoot {
            padding-top: 50px;
        }
       table { 
          width: 100%;
        }

    .col-md-2 {
        width: 16.66666667%;
            float: left;
    }
    .col-md-6 {
        width: 50%;
            float: left;
    }
    .col-md-4 {
        width: 26.333333%;
        float: left;
    }
    .cs {
        font-size: 14px;
        padding: 0px 6px !important;
        margin: -4px 2px !important; 
    }
    label {
        display: inline-block;
        max-width: 100%;
        margin-bottom: 5px;
        font-weight: 700;
    }
    .phead{ font-size: 14px; }
    </style>

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
                                <div id="reciept" style="margin-top: 5px; margin-bottom: 7px;">

                                    <div class="row">
                                        <div class="col-md-12" id="dvContents">
                                            <style>
                                                td {
                                                    padding: 5px;
                                                }
                                            </style>
                                             <table style="width: 100%; border: 1px solid #888;" >
                                                <tr>
                                                    <td> <img src="https://partners.a2zsuvidhaa.com/newlog/images/Logo168.png" style="width:70px;" /></td>
                                                    <td style="text-align:center"> <b>Outlet Name :${data.outletName}<br>Address: ${data.outletAddress}<br>Contact Number: ${data.outletNumber}</b></td>
                                                  <td> <div style="width: 40px;"></div>
                                             </table>
                                            <table style="width: 100%; border: 1px solid #888;" >
                                                <tr>
                                                    
                                                </tr>
                                                <tr style="border-top:1px solid #ddd;" id="trandetailbybps">
                                                    <td>
                                                        <b class="cs" >Biller Name: </b><span id="printBillerName">${data.senderName}</span><br /> 
                                                        <b class="cs" >Customer Mobile No:</b><span id="printCustomerNumber">${data.senderNumber}</span><br />
                                                        <b class="cs" >Payment channel: </b><span id="prt_tranchannel">${data.agentChannel}</span><br />
                                                    </td>
                                                    <td>
                                                        <b class="cs" >Consumer ID/Number : </b><span id="printConsumerNumber">${data.number}</span><br />
                                                        <b class="cs" >Payment Mode :  </b>${data.paymentChannel}<br />
                                                        <b class="cs" >Date & Time : </b><span id="printDate">${data.txnTime}</span>
    													
                                                    </td>
                                                    <td></td>
                                                </tr>
    											<tr>
                                                    <td colspan="3" style="border-bottom: 1px solid #ccc; border-top: 1px solid #ccc;"><b>Transaction Details : &nbsp;&nbsp;&nbsp;&nbsp;       </b><span id="bbps_id">${data.bankRef}</span></td>
                                                    
                                                </tr>
                                                <tr>
                                                    <td colspan="3" class="nospace1">
                                                        <table class="table table-bordered" style="margin-bottom: 2px;" >
    														<thead>
    															<tr style="background:#ddd;">
    																<td class="phead"><b>Date</b></td>
    																<td class="phead"><b>Service Provider</b></td> 
    																<td class="phead"><b>Transaction ID </b></td>
    																<td class="phead"><b>Amount </b></td>
    																<td class="phead"><b>Status </b></td>
    															</tr>
                                                            </thead>
                                                            <tbody id="printTBody">
                                                                <tr>
                                                                    <td>${data.txnTime}</td>
                                                                    <td>${data.provider}</td>
                                                                    <td>${data.reportId}</td>
                                                                    <td>${data.amount}</td>
                                                                    <td>${data.statusDesc}</td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </td>
                                                </tr>
                                              <tr>
                                                    <td colspan="3" style="padding: 0px;" >                                                   
                                                        <div class="cs col-md-12 col-sm-12 col-xs-12">
                                                            <b>Total Amount Rs. : </b>
                                                            <label id="printAmount">${data.amount}</label>&nbsp;&nbsp;&nbsp;
                                                        </div>
                                                        <div class="cs col-md-12 col-sm-12 col-xs-12">
                                                            <b>Amount in Words :</b>
                                                            <label id="prt_tranword">
                                                                ${NumberToWordConverter.doubleConvert(data.amount!!.toDouble())}
                                                            </label>
                                                        </div>
                                                    </td>
                                                </tr>  
                                                 <tr>
                                                       <td colspan="3" style="padding: 0px 8px;" >                
                                                        <b>Disclaimer :</b>
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

}