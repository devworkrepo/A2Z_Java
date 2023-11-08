package com.a2zsuvidhaa.`in`.util.pdf

import com.a2zsuvidhaa.`in`.data.model.TransactionDetail
import com.a2zsuvidhaa.`in`.util.NumberToWordConverter
import java.lang.StringBuilder

fun matmHtmlDataAsString(data : TransactionDetail) : String{


    fun miniStatementTable() : String{

        if(data.miniStatement == null){
            return ""
        }
        else if(data.miniStatement!!.isEmpty()){
            return ""
        }

        val statementBuilder = StringBuilder()
        data.miniStatement?.forEach {
            val value = """
              <tr>
                    <td>${it.txnTime}</td>
                    <td>${it.txnType}</td>
                    <td>${it.narration}</td>
                    <td>${it.amount}</td>                                              
               </tr>
        """.trimIndent()

            statementBuilder.append(value)
        }

       return  """
         <table class="table table-bordered" style="margin-bottom: 2px;" >
        														<thead>
        															<tr style="background:#ddd;">
        																<td class="phead"><b>Date</b></td>
        																<td class="phead"><b>txnType</b></td> 
        																<td class="phead"><b>narration</b></td> 
        																<td class="phead"><b>amount</b></td>
        																
        															</tr>
                                                                </thead>
                                                                <tbody id="printTBody">
                                                                
                                                                ${statementBuilder.toString()}
        														  
        														                                                        </tbody>
                                                            </table>
    """.trimIndent()

    }


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
          /*border-collapse: collapse;*/
          width: 100%;
        }

        </style>


        <div id="myReciept" class="modal fade" role="dialog" data-backdrop="static" data-keyboard="false">
            <div class="modal-dialog" style="margin: 30px auto;">
                <div class="modal-content">
                    <div class="modal-body" style="padding: 10px; border-radius: 0px;">
                        <div class="col-md-12">
                            <button type="button" class="btn" data-dismiss="modal" style="padding: 6px ! important; top: -8px; right: -35px; background-color: rgb(255, 255, 255) ! important; position: absolute;">&times;</button>
                        </div>
                        <div class="containers" style="height: auto; overflow: auto; width: 100%">
                            
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
                                                        <td style="text-align:center"> <b>Outlet Name:${data.outletName}<br>Address: ${data.outletAddress}<br>Contact Number: ${data.outletNumber}</b></td>
                                                       
                                                 </table>
                                                <table style="width: 100%; border: 1px solid #888;" >
                                                    <tr>
                                                        
                                                    </tr>
                                                    <tr style="border-top:1px solid #ddd;" id="trandetailbybps">
                                                        <td>
                                                            <b class="cs" >Card Number : </b>
        													<span id="printBillerName">	${data.number} </span><br /> 
                                                            <b class="cs" >Card Type : </b><span id="printConsumerNumber">${data.cardType}</span><br />
                                                        </td>
                                                        <td>

                                                           
        													<b class="cs" >Customer Mobile No :</b><span id="printCustomerNumber">${data.senderNumber}</span><br />
                                                            <b class="cs" >Txn Type :</b><span id="prt_tranchannel">${data.txnType}</span><br />
                                                        </td>
                                                        <td></td>
                                                    </tr>
        											<tr>
                                                        <td colspan="3" style="border-bottom: 1px solid #ccc; border-top: 1px solid #ccc;"><b>Transaction Details : &nbsp;&nbsp;&nbsp;&nbsp;       </b><span id="bbps_id"></span></td>
                                                        
                                                    </tr>
                                                    <tr>
                                                        <td colspan="3" class="nospace1">
                                                            <table class="table table-bordered" style="margin-bottom: 2px;" >
        														<thead>
        															<tr style="background:#ddd;">
        																<td class="phead"><b>Date</b></td>
        																<td class="phead"><b>Service Provider</b></td> 
        																<td class="phead"><b>Order Id</b></td>
        																<td class="phead"><b>Bank Ref </b></td>
        																<td class="phead"><b>Transaction Amount </b></td>
        																<td class="phead"><b>Avl Amount </b></td>
        																<td class="phead"><b>Status </b></td>
        															</tr>
                                                                </thead>
                                                                <tbody id="printTBody">
                                                                    <tr>
                                                                        <td>${data.txnTime}</td>
                                                                        <td>${data.serviceName}</td>
                                                                        <td>${data.reportId}</td>
                                                                        <td>${data.bankRef}</td>
                                                                        <td>${data.amount}</td>
                                                                        <td>${data.availableBalance}</td>
                                                                        <td>${data.statusDesc}</td>
                                                                    </tr>
                                                                </tbody>
                                                            </table>
                                                        </td>
                                                    </tr>
        																						<tr>
                                                        <td colspan="3" class="nospace1">
                                                           ${miniStatementTable()}
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