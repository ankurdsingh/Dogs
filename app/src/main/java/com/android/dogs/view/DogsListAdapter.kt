package com.android.dogs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.android.dogs.R
import com.android.dogs.databinding.ItemDogBinding
import com.android.dogs.model.DogBreed
import com.android.dogs.util.getProgressDrawable
import com.android.dogs.util.loadImage
import kotlinx.android.synthetic.main.item_dog.view.*

class DogsListAdapter (val dogsList: ArrayList<DogBreed>):RecyclerView.Adapter<RecyclerView.ViewHolder>(),DogClickListener{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
//        val view = inflater.inflate(R.layout.item_dog,parent,false)
        val view = DataBindingUtil.inflate<ItemDogBinding>(inflater,R.layout.item_dog,parent,false)
        return  DogsViewHolder(view)
    }

    override fun getItemCount()=dogsList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(holder is DogsViewHolder){
            holder.view.dog = dogsList[position]
            holder.view.listner = this
        }
        /*    holder.view.name.text = dogsList[position].dogBreed
            holder.view.lifeSpan.text = dogsList[position].lifeSpan
            holder.view.setOnClickListener {
                val  action = ListFragmentDirections.actionDetailFragment()
                action.dogUuid = dogsList[position].uuid
                Navigation.findNavController(it).navigate(action)
            }
            holder.view.image_dog.loadImage(dogsList[position].imageUrl, getProgressDrawable(holder.view.image_dog.context))
        }*/
    }

    override fun onDogClicked(view: View) {
        val uuid = view.dogId.text.toString().toInt()
        val  action = ListFragmentDirections.actionDetailFragment()
        action.dogUuid = uuid
        Navigation.findNavController(view).navigate(action)
    }

    fun update(newDogsList: List<DogBreed>){
        dogsList.clear()
        dogsList.addAll(newDogsList)
        notifyDataSetChanged()
    }

    class DogsViewHolder(var view: ItemDogBinding):RecyclerView.ViewHolder(view.root)
}