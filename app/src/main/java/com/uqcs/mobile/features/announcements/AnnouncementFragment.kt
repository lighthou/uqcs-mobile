package com.uqcs.mobile.features.announcements

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import com.uqcs.mobile.R
import com.uqcs.mobile.common.AuthenticatedFragment
import kotlinx.android.synthetic.main.fragment_announcements.*
import ru.noties.markwon.Markwon
import android.provider.MediaStore
import android.graphics.Bitmap
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.uqcs.mobile.common.CameraPermissionHelper
import com.uqcs.mobile.common.StoragePermissionHelper
import java.io.FileNotFoundException
import java.io.IOException




class AnnouncementFragment : Fragment(), AuthenticatedFragment {

    private val GET_FROM_GALLERY : Int = 3
    private var announcementImage : Bitmap? = null

    companion object {
        fun newInstance(): AnnouncementFragment {
            return AnnouncementFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun setSlackExpand(expand : Boolean) {
        if (expand) {
            slack_expand.background = resources.getDrawable(R.drawable.ic_keyboard_arrow_up)
            collapse_region.visibility = View.VISIBLE
        } else {
            slack_expand.background = resources.getDrawable(R.drawable.ic_keyboard_arrow_down)
            collapse_region.visibility = View.GONE
        }
    }

    private fun setFacebookExpand(expand : Boolean) {
        if (expand) {
            fb_expand.background = resources.getDrawable(R.drawable.ic_keyboard_arrow_up)
            fb_collapse_region.visibility = View.VISIBLE
        } else {
            fb_expand.background = resources.getDrawable(R.drawable.ic_keyboard_arrow_down)
            fb_collapse_region.visibility = View.GONE
        }
    }

    private fun setTwitterExpand(expand : Boolean) {
        if (expand) {
            tt_expand.background = resources.getDrawable(R.drawable.ic_keyboard_arrow_up)
            tt_collapse_region.visibility = View.VISIBLE
        } else {
            tt_expand.background = resources.getDrawable(R.drawable.ic_keyboard_arrow_down)
            tt_collapse_region.visibility = View.GONE
        }
    }

    private fun setLinkedInExpand(expand : Boolean) {
        if (expand) {
            linkedin_expand.background = resources.getDrawable(R.drawable.ic_keyboard_arrow_up)
            linkedin_collapse_region.visibility = View.VISIBLE
        } else {
            linkedin_expand.background = resources.getDrawable(R.drawable.ic_keyboard_arrow_down)
            linkedin_collapse_region.visibility = View.GONE
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        announcements_toolbar.title = "Announcements"
        (activity as AppCompatActivity).setSupportActionBar(announcements_toolbar)
        setUpTextChangeListener()
        setUpCheckboxes()

        fb_expand.setOnClickListener { setFacebookExpand(fb_collapse_region.visibility == View.GONE) }
        slack_expand.setOnClickListener { setSlackExpand(collapse_region.visibility == View.GONE) }
        tt_expand.setOnClickListener { setTwitterExpand(tt_collapse_region.visibility == View.GONE) }
        linkedin_expand.setOnClickListener { setLinkedInExpand(linkedin_collapse_region.visibility == View.GONE) }


        add_image_buttom.setOnClickListener {
            if (!StoragePermissionHelper.hasStoragePermission(this.activity as Activity)) {
                StoragePermissionHelper.requestStoragePermission(this.activity as Activity)
            }
            if (!StoragePermissionHelper.hasStoragePermission(this.activity as Activity)) {
                return@setOnClickListener
            }

                startActivityForResult(
                Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI
                ), GET_FROM_GALLERY
            )
        }

        val list = mutableListOf<String>("#announcements", "#committee", "#general", "#events")
        spinner.setItems(list)
        val spinneritems = spinner.selectedItemsAsString
    }

    private fun setUpTextChangeListener() {
        body_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var sb = StringBuilder()
                val string = s.toString()
                for (c : Int in 0 until string.length) {
                    if (string[c] == '\n') {
                        sb.append("    \n")
                    } else {
                        sb.append(string[c])
                    }
                }
                Markwon.setMarkdown(body_preview,  sb.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        body_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fb_body_preview.text = s.toString()
                tt_body_preview.text = s.toString()
                linkedin_body_preview.text = s.toString()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_announcements, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.announcement_toolbar_menu, menu)

    }

    override fun registerServerCredentials() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun setUpCheckboxes() {
        collapse_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) setSlackExpand(isChecked)
        }

        fb_collapse_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) setFacebookExpand(isChecked)
        }

        tt_collapse_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) setTwitterExpand(isChecked)
        }

        linkedin_collapse_checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) setLinkedInExpand(isChecked)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            val selectedImage = data!!.data
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, selectedImage)
                //add_image_buttom.background = BitmapDrawable(resources, bitmap)
                add_image_buttom.scaleType = ImageView.ScaleType.FIT_CENTER
                add_image_buttom.setImageBitmap(bitmap)
                add_image_buttom.setBackgroundColor(Color.WHITE)
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

        }
    }

}