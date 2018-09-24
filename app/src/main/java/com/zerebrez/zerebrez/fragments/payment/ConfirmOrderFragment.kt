package com.zerebrez.zerebrez.fragments.payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentDialogFragment
import com.zerebrez.zerebrez.models.OrderResponse

import com.zerebrez.zerebrez.models.Provider
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.services.compropago.ComproPagoManager
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.NetworkUtil
import retrofit2.Response

/**
 * Created by Jesus Campos on 05/09/18.
 * jcampos.jc38@gmail.com
 */

class ConfirmOrderFragment: BaseContentDialogFragment(),  ErrorDialog.OnErrorDialogListener{

    private val TAG = "ProvidersFragment"
    private val PRICE = 99.0f
    private var ORDER_GENERATED = false

    private lateinit var mNameEditText: EditText
    private lateinit var mLastNameEditText: EditText
    private lateinit var mEmailEditText: EditText
    private lateinit var mPriceTextView: TextView
    private lateinit var mProvierImageView: ImageView
    private lateinit var mComissionTextView: TextView
    private lateinit var mConfirmOrderButton: Button
    private lateinit var mScrollView: ScrollView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mCloseContainer: RelativeLayout

    private var mProvider: Provider? = null
    private lateinit var mComproPagoManager: ComproPagoManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, R.style.AppTheme)
        //Recover provider information
        mProvider = arguments!!.getSerializable("Provider") as Provider?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.confirm_order_fragment, container, false)!!

        mNameEditText = root.findViewById(R.id.et_order_name)
        mLastNameEditText = root.findViewById(R.id.et_order_last_name)
        mEmailEditText = root.findViewById(R.id.et_order_mail)
        mPriceTextView = root.findViewById(R.id.tv_suscription_price)
        mProvierImageView = root.findViewById(R.id.iv_order_provider_icon)
        mComissionTextView = root.findViewById(R.id.tv_order_provider_comision)
        mConfirmOrderButton = root.findViewById(R.id.btn_order_confirm)
        mScrollView = root.findViewById(R.id.sv_confirm_order)
        mProgressBar = root.findViewById(R.id.pb_confirm_order)
        mCloseContainer = root.findViewById(R.id.rl_close_confirm_order)

        mConfirmOrderButton.setOnClickListener{GenerateOrder()}
        mCloseContainer.setOnClickListener{ activity!!.onBackPressed()}

        mPriceTextView.text = "$${PRICE}"
        mComproPagoManager = ComproPagoManager()

        //Show provider information
        SetEmailIfUser()
        ShowProviderInformation()

        return root
    }

    private fun GenerateOrder() {
        val name = mNameEditText.text.toString()
        val lastName = mLastNameEditText.text.toString()
        val email = mEmailEditText.text.toString()
        if(!name.equals("") && !lastName.equals("") && !email.equals("") && email.contains("@")){
            if (NetworkUtil.isConnected(this!!.activity!!)) {
                setWaitScreen(true)
                mComproPagoManager.GenerateOrder("$name $lastName", email, mProvider!!.internal_name, PRICE, object: ComproPagoManager.OnGenerateOrderListener{
                    override fun onGenerateOrderResponse(response: Response<OrderResponse>?) {
                        OnGenerateOrderSuccess(response)
                    }

                    override fun onGenerateOrderFailure(throwable: Throwable?) {
                        OnGenerateOrderError(throwable)
                    }

                })
            } else
                SendRequestErrorMessage()
        }
        else
            Toast.makeText(activity, "Es necesario llenar todos los campos", Toast.LENGTH_SHORT).show()
    }


    fun OnGenerateOrderSuccess(response: Response<OrderResponse>?){
        if(response!=null){
            if(response.code()<300 && response.code()>199){
                val orderResponse = response.body()
                if (orderResponse != null) {
                    if(!orderResponse.short_id.equals("")) {
                        ORDER_GENERATED=true
                        setPendingPayment(true)
                        setPaymentId(orderResponse.id)
                        ErrorDialog.newInstance("Orden de pago generada", "Las instrucciones de pago llegarán al corrreo proporcionado, una vez realizado el pago obtendrás tu suscripción.",
                                DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "OrderGenerated")
                    }else
                        SendOrderErrorMessage()
                }else
                    SendOrderErrorMessage()
            }else
                SendOrderErrorMessage()
        }else
            SendOrderErrorMessage()
    }

    fun OnGenerateOrderError(throwable: Throwable?){
        SendOrderErrorMessage()
    }

    private fun setWaitScreen(set: Boolean) {
        mScrollView.setVisibility(if (set) View.GONE else View.VISIBLE)
        mProgressBar.setVisibility(if (set) View.VISIBLE else View.GONE)
    }

    private fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }

    private fun ShowProviderInformation(){
        if(mProvider!=null){
            mProvierImageView.loadUrl(mProvider!!.image_small)
            mComissionTextView.text = getString(R.string.comission_text, mProvider!!.commission.toFloat())
        }
    }


    private fun SetEmailIfUser(){
        val userFirebase = FirebaseAuth.getInstance().currentUser

        if (userFirebase != null) {
            mEmailEditText.setText(userFirebase.getEmail())
        }
        else
            requestGetProfileRefactor()
    }

    override fun onGetProfileRefactorSuccess(user: User) {
        super.onGetProfileRefactorSuccess(user)
        mEmailEditText.setText(user.getEmail())
    }


    fun SendRequestErrorMessage(){
        Log.i(TAG, "onErrorOrderRequest() Failed to enqueue")
        ErrorDialog.newInstance("Algo salió mal...", "La orden de pago no pudo ser generada. Asegurate de tener una conexión a internet.",
                DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
    }

    fun SendOrderErrorMessage(){
        ErrorDialog.newInstance("Algo salió mal...", "La orden de pago no pudo ser generada. Asegurate de haber proporcionado un correo válido",
                DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "OrderError")
    }

    override fun onConfirmationCancel() {

    }

    override fun onConfirmationNeutral() {
        if (ORDER_GENERATED)
            activity!!.finish()
        else
            setWaitScreen(false)
    }

    override fun onConfirmationAccept() {

    }
}