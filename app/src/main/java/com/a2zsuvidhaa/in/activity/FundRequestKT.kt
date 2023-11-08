package com.a2zsuvidhaa.`in`.activity


/*
class FundRequestActivity : AppCompatActivity(), OnDatePicker {

    private lateinit var binding: ActivityFundRequestBinding

    private val bankListHashMap = HashMap<String, String>()
    private val paymentModeHashMap = LinkedHashMap<String, String>()
    private val onlineModeHashMap = LinkedHashMap<String, String>()
    private val approvals = arrayOfNulls<String>(2)
    private var picUri: Uri? = null
    private var strBankId: String = ""
    private var strBankName = ""
    private var strPaymentMode: String = ""
    private var strPaymentDate = ""
    private var strRequestTo = ""
    private var strAmount = ""
    private var strRemark = ""
    private var strRefNumber = ""
    private var strOnlineMode: String = ""
    private var get_from: String? = "0"
    private val get_acc = ""
    private val get_ifsc = ""
    private val get_reqTo = ""
    private var selectedFilePath: String = ""
    private var selectedFileMimeType: String = ""
    private lateinit var dbHelper: DBHelper
    var type: String = "1"
    var mode: String = ""
    lateinit var bankDetail: BankDetail


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFundRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.extras != null) {
            type = intent.extras!!.getString("type")!!
            mode = intent.extras!!.getString("mode")!!
            bankDetail = intent.extras!!.getParcelable("bank")!!
        }
        if (intent.extras != null) {
            get_from = intent.extras!!.getString("")
            get_from = intent.extras!!.getString("")
            get_from = intent.extras!!.getString("")
            get_from = intent.extras!!.getString("")
        }
        init()
        requestTo()
        setupPaymentMode()
        dbHelper = DBHelper(this)
        binding.btnRequestPayment.setOnClickListener { view: View? ->
            if (isFieldValid) {
                val confirmDialog: Dialog = AppDialogs.confirmFundRequestDialog(this)
                val tv_requestTo = confirmDialog.findViewById<TextView>(R.id.tv_requestTo)
                val tv_paymentMode = confirmDialog.findViewById<TextView>(R.id.tv_paymentMode)
                val tv_paymentDate = confirmDialog.findViewById<TextView>(R.id.tv_paymentDate)
                val tv_toAccount = confirmDialog.findViewById<TextView>(R.id.tv_toAccount)
                val tv_refNumber = confirmDialog.findViewById<TextView>(R.id.tv_refNumber)
                val tv_approveDetail = confirmDialog.findViewById<TextView>(R.id.tv_approveDetail)
                val tv_amount = confirmDialog.findViewById<TextView>(R.id.tv_amount)
                val ed_confirmAmount = confirmDialog.findViewById<EditText>(R.id.ed_confirmAmount)
                val btn_close = confirmDialog.findViewById<Button>(R.id.btn_close)
                val btn_confirmTransfer = confirmDialog.findViewById<Button>(R.id.btn_confirmTransfer)
                val ll_companyFields = confirmDialog.findViewById<LinearLayout>(R.id.ll_companyFields)
                if (strRequestTo.equals("2", ignoreCase = true)) ll_companyFields.visibility = View.VISIBLE else ll_companyFields.visibility = View.GONE
                tv_requestTo.text = binding.spnRequestTo.selectedItem.toString()
                tv_paymentMode.text = binding.spnPaymentMode.selectedItem.toString()
                tv_paymentDate.text = strPaymentDate
                if (type.equals("2", ignoreCase = true)) tv_toAccount.text = binding.spnToAccount.selectedItem.toString()
                tv_refNumber.text = strRefNumber
                tv_approveDetail.text = binding.tvApprovalDetail.text.toString()
                tv_amount.text = strAmount
                btn_close.setOnClickListener { view12: View? -> confirmDialog.dismiss() }
                btn_confirmTransfer.setOnClickListener { view2: View? ->
                    if (strAmount.equals(ed_confirmAmount.text.toString(), ignoreCase = true)) {
                        SoftKeyboard.hide(this)
                        confirmDialog.dismiss()
                        if (dbHelper.isNumberExist(strRefNumber, strAmount)) {
                            if (dbHelper.canTransaction(strRefNumber, AppUitls.getTime())) {
                                requestPayment("update")
                            } else {
                                val dialog: Dialog = AppDialogs.transactionStatus(this,
                                        """
                                            Request of same amount with same Reference Id has been send successfully
                                            Please wait for 2 minutes
                                            """.trimIndent(), 3)
                                val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
                                btn_ok.setOnClickListener { view1: View? -> dialog.dismiss() }
                                dialog.show()
                            }
                        } else {
                            requestPayment("insert")
                        }
                    } else MakeToast.show(this, "Confirm amount does not matched!")
                }
                confirmDialog.show()
            }
        }
        binding.tvPaymentDate.setOnClickListener { DatePicker.datePicker(this) }

        binding.btnImageUpload.setOnClickListener { PermissionHandler.checkStorageAndCameraPermission(this) }

     */
/*   PermissionHandler.setPermissionGrantedListener { isGranted: Boolean ->
            if (isGranted) {
                Dialogs.fileAndImageChooser(this) { isFile, isGallery, isCamera ->
                    when {
                        isFile -> PickerHelperActivity.pickFile(this)
                        isGallery -> PickerHelperActivity.pickImage(this)
                        isCamera -> PickerHelperActivity.cameraCapture(this)
                    }
                }
            }
        }*//*

        DatePicker.setupOnDatePicker(this)
        if (type.equals("2", ignoreCase = true)) bankList
        setupRefPlaceHolder()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {


         */
/*   val uri : Uri? = if(PickerHelperActivity.CAMERA_PICKER_CODE == requestCode)
            {
                val bitmap = data?.extras?.get("data") as Bitmap
                getImageUriForCameraCapture(bitmap)
            }
            else  data?.data*//*


           */
/* selectedFilePath = FileUtils.getPath(this, uri)
            selectedFileMimeType = FileUtils.getMimeType(this,uri)


            val fileName = FileUtils.getFileName(this, uri)
            binding.tvFileName.text = fileName*//*

        }
    }

    private fun getImageUriForCameraCapture(photo: Bitmap): Uri? {
        return StorageHelper.saveImageToExternalStorage(
                context = this,
                fileName = "a2z_suvidhaa_fund_request_file_"
                        + System.currentTimeMillis().toString(),
                photo
        )
    }


    private fun setupRefPlaceHolder() {
        if (mode.equals("BT", ignoreCase = true)) binding.edRefNumber.setHint(bankDetail.bank_transfer_place_holder)
        if (mode.equals("CASH_CDM", ignoreCase = true)) binding.edRefNumber.setHint(bankDetail.cash_cdm_place_holder)
        if (mode.equals("CASH_DEPOSIT", ignoreCase = true)) binding.edRefNumber.setHint(bankDetail.cash_deposit_place_holder)
    }

    private fun requestTo() {
        var agentType = "Distributor"
        if (SharedPref.getInstance(this).getRollId() == 4) agentType = "Master Distributor"
        val requestToList = arrayOfNulls<String>(1)
        if (type.equals("1", ignoreCase = true)) {
            binding.llToAccount.visibility = View.GONE
            binding.spnRequestTo.visibility = View.VISIBLE
            requestToList[0] = agentType
        } else {
            binding.spnToAccount.visibility = View.VISIBLE
            binding.spnRequestTo.visibility = View.GONE
            requestToList[0] = "Company"
        }
        val dataAdapter = ArrayAdapter(this,
                R.layout.spinner_layout, requestToList)
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout)
        binding.spnRequestTo.adapter = dataAdapter
        binding.spnRequestTo.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (binding.spnRequestTo.selectedItem.toString().equals(requestToList[0], ignoreCase = true)) {
                    binding.tvApprovalDetail.text = approvals[0]
                    strRequestTo = "1"
                    binding.edRefNumber.setText("")
                    //setupPaymentMode(1);
                    binding.llToAccount.visibility = View.GONE
                    Log.e("strRequestTo 1", "=")
                } else {
                    binding.tvApprovalDetail.text = approvals[1]
                    strRequestTo = "2"
                    // setupPaymentMode(2);
                    binding.llToAccount.visibility = View.VISIBLE
                    Log.e("strRequestTo 2", "=")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupPaymentMode() {
        paymentModeHashMap.clear()
        */
/*if (requestTo == 1) {
            paymentModeHashMap.put("Cash", "Cash");
            paymentModeHashMap.put("Cheque", "Cheque");
            paymentModeHashMap.put("OnLine", "OnLine");

        } else {
            //paymentModeHashMap.put("Cheque", "Cheque");
            paymentModeHashMap.put("OnLine", "OnLine");
            paymentModeHashMap.put("cash@Counte", "cash@Counte");
            paymentModeHashMap.put("Cash@CDM", "Cash@CDM");



        }*//*
if (type.equals("2", ignoreCase = true)) {
            strRequestTo = "2"
            if (MainActivity.mode_pos == 1) {
                paymentModeHashMap["OnLine"] = "OnLine"
            } else if (MainActivity.mode_pos == 2) {
                paymentModeHashMap["Cash@CDM"] = "Cash@CDM"
            } else if (MainActivity.mode_pos == 3) {
                paymentModeHashMap["cash@Counter"] = "cash@Counter"
            } else {
                paymentModeHashMap["Cash@Collect"] = "Cash@Collect"
            }
        } else {
            strRequestTo = "1"
            paymentModeHashMap["Cash"] = "Cash"
            paymentModeHashMap["Cheque"] = "Cheque"
            paymentModeHashMap["OnLine"] = "OnLine"
        }
        val paymentModeList = paymentModeHashMap.keys.toTypedArray()
        val paymentModeAdapter = ArrayAdapter(this,
                R.layout.spinner_layout, paymentModeList)
        paymentModeAdapter.setDropDownViewResource(R.layout.spinner_layout)
        binding.spnPaymentMode.adapter = paymentModeAdapter
        binding.spnPaymentMode.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                strPaymentMode = paymentModeHashMap[binding.spnPaymentMode.selectedItem.toString()]!!
                if (strRequestTo == "1") {
                    binding.llOnlineMode.visibility = View.GONE
                    if (strPaymentMode != null) {
                        if (strPaymentMode.equals("Online", ignoreCase = true)
                                || strPaymentMode.equals("Cheque", ignoreCase = true)) {
                            binding.llBankName.visibility = View.VISIBLE
                            binding.llRefNumber.visibility = View.VISIBLE
                        } else {
                            binding.llBankName.visibility = View.GONE
                            binding.llRefNumber.visibility = View.GONE
                        }
                    }
                } else {
                    binding.llRefNumber.visibility = View.VISIBLE
                    if (strPaymentMode.equals("Online", ignoreCase = true)) {
                        binding.llOnlineMode.visibility = View.VISIBLE
                        setupOnlineMode()
                    } else {
                        binding.llOnlineMode.visibility = View.GONE
                    }
                }
                if (binding.llBankName.visibility == View.GONE) {
                    strBankName = ""
                    binding.edBankName.setText("")
                }
                if (binding.llRefNumber.visibility == View.GONE) {
                    strRefNumber = ""
                    binding.edRefNumber.setText("")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupOnlineMode() {
        onlineModeHashMap.clear()
        onlineModeHashMap["IMPS"] = "IMPS"
        onlineModeHashMap["NEFT"] = "NEFT"
        onlineModeHashMap["RTGS"] = "RTGS"
        onlineModeHashMap["OTHER"] = "OTHER"
        val paymentModeList = onlineModeHashMap.keys.toTypedArray()
        val paymentModeAdapter = ArrayAdapter(this,
                R.layout.spinner_layout, paymentModeList)
        paymentModeAdapter.setDropDownViewResource(R.layout.spinner_layout)
        binding.spnOnlineMode.adapter = paymentModeAdapter
        binding.spnOnlineMode.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                strOnlineMode = onlineModeHashMap[binding.spnOnlineMode.selectedItem.toString()]!!
                if (binding.llOnlineMode.visibility == View.GONE) {
                    strOnlineMode = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun init() {

        binding.imgBtnBack.setOnClickListener { view: View? -> onBackPressed() }
        binding.tvPaymentDate.setText(AppUitls.currentDate())
        strPaymentDate = AppUitls.currentDate()

    }

    private fun requestPayment(insertOrUpdate: String) {
        setProgressVisibility(true, 1)
        val request: StringRequest = object : StringRequest(Method.POST,
                APIs.FUND_REQUEST_SAVE,
                Response.Listener { response: String? ->
                    try {
                        val jsonObject = JSONObject(response)
                        Log.e("resp req fund", "=$jsonObject")
                        val status = jsonObject.getString("status")
                        val message = jsonObject.getString("message")
                        if (status.equals("1", ignoreCase = true)) {
                            clearInput()
                            val dialog: Dialog = AppDialogs.transactionStatus(this, message, 1)
                            dialog.show()
                            val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
                            btn_ok.setOnClickListener { view: View? -> dialog.dismiss() }
                            dialog.setOnDismissListener { dialogInterface: DialogInterface? ->
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                            if (insertOrUpdate.equals("update", ignoreCase = true)) {
                                if (!dbHelper.updateTransaction(strRefNumber, AppUitls.getTimeFuture2Miuntes().toString(), strAmount)) {
                                    MakeToast.show(this, """
     failed to insert record into database, but
     you don't have to be worry!
     please once contact to developer thankyou
     """.trimIndent())
                                }
                            } else if (insertOrUpdate.equals("insert", ignoreCase = true)) {
                                if (!dbHelper.saveTransaction(strRefNumber, "fund_requests",
                                                "mobile", AppUitls.getTimeFuture2Miuntes().toString(), strAmount)) {
                                    MakeToast.show(this, """
     failed to insert record into database, but
     you don't have to be worry!
     please once contact to developer thankyou
     """.trimIndent())
                                }
                            }
                        } else if (status.equals("200", ignoreCase = true)) {
                            val intent = Intent(this, AppInProgressActivity::class.java)
                            intent.putExtra("message", message)
                            intent.putExtra("type", 0)
                            startActivity(intent)
                        } else if (status.equals("300", ignoreCase = true)) {
                            val intent = Intent(this, AppInProgressActivity::class.java)
                            intent.putExtra("message", message)
                            intent.putExtra("type", 1)
                            startActivity(intent)
                        } else {
                            val dialog: Dialog = AppDialogs.transactionStatus(
                                    this, message, 2)
                            dialog.show()
                            val btn_ok = dialog.findViewById<Button>(R.id.btn_ok)
                            btn_ok.setOnClickListener { view: View? -> dialog.dismiss() }
                        }
                    } catch (e: JSONException) {
                        binding.tvIncorrect.visibility = View.VISIBLE
                    }
                    setProgressVisibility(false, 1)
                },
                Response.ErrorListener { error: VolleyError? ->
                    setProgressVisibility(false, 1)
                    binding.tvIncorrect.visibility = View.VISIBLE
                }) {
            override fun getParams(): Map<String, String> {
                val param = HashMap<String, String>()
                param["userId"] = SharedPref.getInstance(this@FundRequestActivity).getId().toString()
                param["token"] = SharedPref.getInstance(this@FundRequestActivity).getToken().toString()
                param["amount"] = strAmount
                param["requestTo"] = strRequestTo
                param["paymentDate"] = strPaymentDate
                param["paymentMode"] = strPaymentMode
                param["refNumber"] = strRefNumber
                param["onlineMode"] = strOnlineMode
                param["bankId"] = strBankId
                param["bankName"] = strBankName
                Log.e("prams fund req", "=$param")
                if (selectedFilePath != null) param["d_picture"] = StorageHelper.fileToString(selectedFilePath)!!
                param["remark"] = strRemark
                param["mime_type"] = selectedFileMimeType
                return param
            }
        }
        RequestHandler.getInstance(this).addToRequestQueue(request)
        request.retryPolicy = DefaultRetryPolicy(
                TimeUnit.SECONDS.toMillis(20).toInt(),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
    }

    private val isFieldValid: Boolean
        private get() {
            var isValid = false
            strAmount = binding.edAmount.text.toString()
            strRefNumber = binding.edRefNumber.text.toString()
            strRemark = binding.edRemark.text.toString()
            strBankName = binding.edBankName.text.toString()
            if (InternetConnection.isConnected(this)) {
                if (!strBankId.equals("", ignoreCase = true) || strRequestTo.equals("1", ignoreCase = true)) {
                    binding.tvIncorrect.visibility = View.GONE
                    if (!strAmount.isEmpty()) {
                        if (!strBankName.isEmpty() || binding.llBankName.visibility == View.GONE) {
                            if (!strRefNumber.isEmpty() || binding.llRefNumber.visibility == View.GONE) {
                                if (type.equals("2", ignoreCase = true)) {
                                    if (selectedFilePath != null) {
                                        isValid = true
                                    } else MakeToast.show(this, "Please Upload Payment Slip.")
                                } else isValid = true
                            } else MakeToast.show(this, "Bank Ref can't be empty!")
                        } else MakeToast.show(this, "Bank name can't be empty!")
                    } else MakeToast.show(this, "Amuont can't be empty!")
                } else MakeToast.show(this,
                        "Request can't be approve !\nBank Name not found!\ntry after sometime")
            }
            return isValid
        }
    val bankList: Unit
        get() {
            val url: String = APIs.FUND_REQUEST_BANK_LIST + "?"
            setProgressVisibility(true, 0)
            WebApiCall.getRequest(this, url)
            WebApiCall.webApiCallback(object : WebApiCallListener {
                override fun onSuccessResponse(jsonObject: JSONObject) {
                    setProgressVisibility(false, 0)
                    try {
                        val status = jsonObject.getString("status")
                        val message = jsonObject.getString("message")
                        if (status.equals("1", ignoreCase = true)) {
                            val bankObject = jsonObject.getJSONObject("banks")
                            setBankList(bankObject)
                            val approvalObject = jsonObject.getJSONObject("approvalDetail")
                            approvals[0] = approvalObject.getString("message1")
                            approvals[1] = approvalObject.getString("message2")
                            binding.tvApprovalDetail.text = approvals[0]
                        } else if (status.equals("200", ignoreCase = true)) {
                            val intent = Intent(this@FundRequestActivity, AppInProgressActivity::class.java)
                            intent.putExtra("message", message)
                            intent.putExtra("type", 0)
                            startActivity(intent)
                        } else if (status.equals("300", ignoreCase = true)) {
                            val intent = Intent(this@FundRequestActivity, AppInProgressActivity::class.java)
                            intent.putExtra("message", message)
                            intent.putExtra("type", 1)
                            startActivity(intent)
                        } else MakeToast.show(this@FundRequestActivity, message)
                    } catch (e: JSONException) {
                    }
                }

                override fun onFailure(message: String) {
                    setProgressVisibility(false, 0)
                }
            })
        }

    private fun setBankList(pmethodObject: JSONObject) {
        try {
            bankListHashMap.clear()
            val iterator: Iterator<*> = pmethodObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next() as String
                if (bankDetail.id.equals(key, ignoreCase = true)) bankListHashMap[pmethodObject.getString(key)] = key
            }
            val prepaidStrings = bankListHashMap.keys.toTypedArray()
            val dataAdapter = ArrayAdapter(this,
                    R.layout.spinner_layout, prepaidStrings)
            dataAdapter.setDropDownViewResource(R.layout.spinner_layout)
            binding.spnToAccount.adapter = dataAdapter
            binding.spnToAccount.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                    strBankId = bankListHashMap[binding.spnToAccount.selectedItem.toString()]!!
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setProgressVisibility(b: Boolean, type: Int) {
        if (type == 0) { // initial progress
            if (b) {
                binding.progressBarToAccount.visibility = View.VISIBLE
            } else {
                binding.progressBarToAccount.visibility = View.GONE
            }
        } else { // on button click request fund payment
            if (b) {
                binding.btnRequestPayment.visibility = View.GONE
                binding.rlProgress.visibility = View.VISIBLE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.btnRequestPayment.visibility = View.VISIBLE
                binding.rlProgress.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("pic_uri", picUri)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        picUri = savedInstanceState.getParcelable("pic_uri")
    }

    override fun onPostResume() {
        super.onPostResume()
        AutoLogoutManager.cancelTimer()
        if (AutoLogoutManager.isSessionTimeout) {
            AutoLogoutManager.logout(this)
        }
    }

    override fun onStop() {
        super.onStop()
        if (AutoLogoutManager.isAppInBackground(this)) {
            AutoLogoutManager.startUserSession()
        }
    }

    private fun clearInput() {
        binding.edAmount.setText("")
        binding.edRefNumber.setText("")
        binding.edRemark.setText("")
    }

    override fun onDatePick(date: String) {
        binding.tvPaymentDate.text = date
        strPaymentDate = date
    }

    companion object {
        private const val TAG = "FundRequestActivity"
        private const val IMAGE_RESULT = 200
    }
}*/
