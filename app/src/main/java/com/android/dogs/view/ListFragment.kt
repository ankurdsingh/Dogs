package com.android.dogs.view


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import com.android.dogs.R
import com.android.dogs.viewmodel.DogListViewModel
import kotlinx.android.synthetic.main.fragment_list.*

/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {
    private lateinit var viewModel : DogListViewModel
    private val dogsListAdapter: DogsListAdapter = DogsListAdapter(arrayListOf())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DogListViewModel::class.java)
        viewModel.refresh()

        dogsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter  = dogsListAdapter
        }
        refreshLayout.setOnRefreshListener {
            dogsList.visibility = View.GONE
            listError.visibility = View.GONE
            loadingView.visibility = View.GONE
            viewModel.refreshByPassCache()
            refreshLayout.isRefreshing = false
        }
        observeViewModel()
       // ListFragmentDirections.actionDetailFragment()
    }

    private fun observeViewModel() {
        viewModel.dogs.observe(this, Observer {dogs->
            dogs?.let {
                dogsList.visibility= View.VISIBLE
                dogsListAdapter.update(dogs)
            }
        })
        viewModel.isError.observe(this, Observer { error->
            error?.let{
                listError.visibility = if(error) View.VISIBLE else View.GONE
            }
        })
        viewModel.isLoading.observe(this, Observer { loading->
            loadingView.visibility = if(loading) View.VISIBLE else View.GONE
            if(loading){
                listError.visibility = View.GONE
                dogsList.visibility = View.GONE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionSettings->{
                view?.let {  Navigation.findNavController(it).navigate(ListFragmentDirections.actionSettings())}
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
