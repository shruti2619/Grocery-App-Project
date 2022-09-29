package com.example.mystore.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.mystore.MainActivity
import com.example.mystore.R
import com.example.mystore.databinding.ActivityProductDetailBinding
import com.example.mystore.databinding.ItemCategoryProductLayoutBinding
import com.example.mystore.roomdb.AppDatabase
import com.example.mystore.roomdb.ProductDao
import com.example.mystore.roomdb.ProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.jar.Attributes

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductDetailBinding.inflate(layoutInflater)

        getProductDetails(intent.getStringExtra("id"))
        setContentView(binding.root)
    }

    private fun getProductDetails(proId: String?) {
        Firebase.firestore.collection("products")
            .document(proId!!).get().addOnSuccessListener {
                val list = it.get("productImages") as ArrayList<String>

                val name = it.getString("productName")
                val productSp = it.getString("productSp")
                val productDesc = it.getString("productDescription")

                binding.textView7.text= it.getString("productName")
                binding.textView8.text= it.getString("productSp")
                binding.textView9.text= it.getString("productDescription")

                val slidelist =ArrayList<SlideModel>()
                for (data in list){
                    slidelist.add(SlideModel(data, ScaleTypes.CENTER_CROP))
                }

                cartAction(proId, name , productSp,it.getString("productCoverImg"))
                binding.imageSlider.setImageList(slidelist)

            }.addOnFailureListener{
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cartAction(proId: String, name: String?, productSp: String?, coverImg: String?){
        val productDao = AppDatabase.getInstance(this).productDao()

        if (productDao.isExit(proId)!= null){
            binding.textView10.text ="Go to cart"
        }else{
            binding.textView10.text="Add to Cart"
        }

        binding.textView10.setOnClickListener{
            if (productDao.isExit(proId)!= null){
                openCart()
            }else{
                addToCart(productDao,proId,name,productSp,coverImg)
            }
        }

    }

    private fun addToCart(productDao: ProductDao, proId: String, name: String?, productSp: String?, coverImg: String?) {
        val data = ProductModel(proId,name,coverImg,productSp)
        lifecycleScope.launch(Dispatchers.IO){
            productDao.insertProduct(data)
            binding.textView10.text = "Go to Cart"
        }
    }

    private fun openCart() {
        val preference = this.getSharedPreferences("info", MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", true)
        editor.apply()

        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}