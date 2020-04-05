/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        //getting reference to the application context
        //requireNotNull throws an IllegalArgumentException if value is null
        val application = requireNotNull(this.activity).application


        //you need a reference to your data source via reference to the DAO
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao


        //instance of ViewModel factory
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)


        //viewModel Reference
        val viewModel =
                ViewModelProvider(this, viewModelFactory)
                        .get(SleepTrackerViewModel::class.java)

        // setting the fragment as the lifecycle owner
        binding.lifecycleOwner = this

        /* set ViewModel for Data Binding - this allows the layout to access all data in the
          ViewModel*/

        binding.sleepTrackerViewModel = viewModel

        viewModel.navigateToSleepQuality.observe(viewLifecycleOwner, Observer { night ->
            night?.let {
                this.findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment
                (night.nightId))
                viewModel.doneNavigating()

            }
        })


        viewModel.showSnackBarEvent.observe(viewLifecycleOwner, Observer {


            if (it == true) {

                Snackbar.make(requireActivity().findViewById(android.R.id.content), getString(R.string
                        .cleared_message),
                        Snackbar.LENGTH_SHORT).show()
            }
        })
        //RecyclerView Starts here

        //adapter instance
        val adapter = SleepNightAdapter(SleepNightListener {
                id->

            //Toast.makeText(context, "$id", Toast.LENGTH_SHORT).show()

            viewModel.onSleepNightClicked(id)
        })

        // telling recyclerView about the adapter
        binding.recyclerView.adapter = adapter
        /* The RecyclerView(sleepList) is now an extension property for
         Fragment, and it has the same type as declared the.xml. */

        //observing SleepNight properties change in Database
        viewModel.nights.observe(viewLifecycleOwner, Observer {

            //let {} returns a copy of the changed object i.e. sleepNight
            it.let { adapter.addHeaderAndSubmitList(it) }

            /*when submitList() is called, the ListAdapter diffs the new list against
            the old one then the listAdapter updates the items*/
        })


        viewModel.navigatetoSleepDetail.observe(viewLifecycleOwner, Observer { night -> night?.let {

            this.findNavController().navigate(SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepDetailFragment(night))
            viewModel.onSleepDetailNavigated()
        }  })


        //getting GridLayout - short constructor

        val manager = GridLayoutManager(activity, 3)

        /*determining how many spans to use for each item in list
        * You need to make an object because setSpanSizeLookup doesn't take a lambda
        * To make an object in Kotlin type object:classname
        * */
 manager.spanSizeLookup = object:GridLayoutManager.SpanSizeLookup(){
     override fun getSpanSize(position: Int): Int {
      return   when(position){

             0 -> 3
          else -> 1
         }
     }
 }
/*
        //getting GridLayout - long constructor
        val manager = GridLayoutManager(activity,3,
                GridLayoutManager.VERTICAL, false)
*/



        //telling recyclerView to use GridLayoutManager
        binding.recyclerView.layoutManager = manager

        return binding.root
    }
}
