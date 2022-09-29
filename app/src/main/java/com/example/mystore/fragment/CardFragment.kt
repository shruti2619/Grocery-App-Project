package com.example.mystore.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.mystore.R
import com.example.mystore.activity.AddressActivity
import com.example.mystore.activity.CategoryActivity
import com.example.mystore.activity.ProductDetailActivity
import com.example.mystore.adapter.CartAdapter
import com.example.mystore.databinding.FragmentCardBinding
import com.example.mystore.roomdb.AppDatabase
import com.example.mystore.roomdb.ProductModel


class CardFragment : Fragment() {

    private lateinit var binding:FragmentCardBinding
    private lateinit var list: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCardBinding.inflate(layoutInflater)

        val preference = requireContext().getSharedPreferences("info", AppCompatActivity.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean("isCart", false)
        editor.apply()
        // Inflate the layout for this fragment

        val dao = AppDatabase.getInstance(requireContext()).productDao()

        list =ArrayList()
        dao.getAllProducts().observe(requireActivity()){
            binding.cartRecycler.adapter = CartAdapter(requireContext(),it)

            list.clear()
            for (data in it){
                list.add(data.productId)
            }
            totalCost(it)
        }
        return binding.root
    }

    private fun totalCost(data: List<ProductModel>?) {
        var total = 0
        for(item in data!!){
            total+=item.productSp!!.toInt()
        }
        binding.textView12.text= "Total item in cart is ${data.size}"
        binding.textView13.text= "Total Cost : $total"

        binding.checkout.setOnClickListener {
            val intent= Intent(context, AddressActivity::class.java)
            val b= Bundle()
            b.putStringArrayList("productIds", list)
            b.putString("totalCost", total.toString())
            intent.putExtras(b)
            startActivity(intent)
        }

    }



    }
