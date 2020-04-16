package com.android.dogs.view


import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.palette.graphics.Palette

import com.android.dogs.R
import com.android.dogs.databinding.FragmentDetailBinding
import com.android.dogs.databinding.SendSmsDialogBinding
import com.android.dogs.model.DogBreed
import com.android.dogs.model.DogPalette
import com.android.dogs.model.SmsInfo
import com.android.dogs.util.getProgressDrawable
import com.android.dogs.util.loadImage
import com.android.dogs.viewmodel.DetailViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.item_dog.view.*

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {
    private var dogUuid = 0
    private lateinit var dogViewModel: DetailViewModel
    private lateinit var dataBinding: FragmentDetailBinding
    private var sendSmSStarted = false
    private var currentDog: DogBreed? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return dataBinding.root
//        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dogViewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
        }
        dogViewModel.fetch(dogUuid)
        observeData()
    }

    private fun observeData() {
        dogViewModel.dogLiveData.observe(this, Observer { dog ->
            currentDog = dog
            dataBinding.dogDetails = dog
            dog.imageUrl?.let {
                setupBgColor(it)
            }
            /*dogName.text = dog.dogBreed
            dogPurpose.text = dog.breedFor
            dogTemperament.text = dog.temperament
            dogLifespan.text = dog.lifeSpan
            context?.let {
                dogImage.loadImage(dog.imageUrl, getProgressDrawable(it))
            }*/
        })
    }

    private fun setupBgColor(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource).generate { palette ->
                        val intColor = palette?.lightMutedSwatch?.rgb ?: 0
                        val myPalette = DogPalette(intColor)
                        dataBinding.palette = myPalette
                    }
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send_sms -> {
                sendSmSStarted = true
                (activity as MainActivity).checkSmsPermission()
            }
            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "type/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT,"Check out tis dog bread")
                intent.putExtra(Intent.EXTRA_TEXT,"${currentDog?.dogBreed} brad for ${currentDog?.breedFor}")
                intent.putExtra(Intent.EXTRA_STREAM,currentDog?.imageUrl)
                startActivity(Intent.createChooser(intent,"Share with"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (sendSmSStarted && permissionGranted) {
            context?.let {
                val smsInfo = SmsInfo(
                    "",
                    "${currentDog?.dogBreed} brad for ${currentDog?.breedFor}",
                    currentDog?.imageUrl
                )
                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    LayoutInflater.from(it),
                    R.layout.send_sms_dialog,
                    null,
                    false
                )
                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send SMS"){dialog, which ->
                        if (!dialogBinding.smsDestination.text.isNullOrEmpty()){
                            smsInfo.to = dialogBinding.smsDestination.text.toString()
                            sendSms(smsInfo)
                        }

                    }
                    .setNegativeButton("cancel"){dialog, which ->  }
                    .show()
                dialogBinding.smsInfo = smsInfo
            }
        }
    }

    private fun sendSms(smsInfo: SmsInfo) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context,0,intent,0)
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(smsInfo.to,null,smsInfo.text,pendingIntent,null)
    }
}
