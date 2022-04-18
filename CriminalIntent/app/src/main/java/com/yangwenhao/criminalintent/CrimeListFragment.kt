package com.yangwenhao.criminalintent

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null
    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var noCrimeText : TextView
    private lateinit var addButton: Button
    private var adapter: CrimeAdapter = CrimeAdapter()

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        addButton = view.findViewById(R.id.add_button)
        noCrimeText = view.findViewById<TextView>(R.id.no_crime_text)
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(view, crimes)
                }
            }
        )
        addButton.setOnClickListener {
            addCrime()
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                addCrime()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun addCrime() {
        val crime = Crime()
        crimeListViewModel.addCrime(crime)
        callbacks?.onCrimeSelected(crime.id)
    }

    private fun updateUI(view: View, crimes: List<Crime>) {
        Log.i(TAG, "update UI...")
        if (crimes.isEmpty()) {
            noCrimeText.visibility = View.VISIBLE
            addButton.visibility = View.VISIBLE
            crimeRecyclerView.visibility = View.GONE
        } else {
            noCrimeText.visibility = View.GONE
            addButton.visibility = View.GONE
            crimeRecyclerView.visibility = View.VISIBLE
            crimeRecyclerView.adapter =  CrimeAdapter().apply {
                submitList(crimes)
            }
        }
//        noCrimeText.visibility = if (crimes.isEmpty()) View.VISIBLE else View.GONE
//
//        crimeRecyclerView.adapter =  CrimeAdapter().apply {
//            submitList(crimes)
//        }
    }

    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var crime: Crime
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val linkPoliceButton: Button? = itemView.findViewById(R.id.link_police)
        private val solvedImagedView: ImageView? = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
            linkPoliceButton?.setOnClickListener {
                Toast.makeText(context,"Calling 110...", Toast.LENGTH_SHORT).show()
            }
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = DateFormat.format("EEEE, MMM d, yyyy", this.crime.date)
            solvedImagedView?.visibility = if (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }


    private inner class CrimeAdapter : ListAdapter<Crime, CrimeHolder> {

        constructor() : super(object: DiffUtil.ItemCallback<Crime>() {
            override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean {
                return oldItem == newItem
            }
        })

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view =
                if (viewType == ViewType.REGULAR_VIEW)
                    layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                else
                    layoutInflater.inflate(R.layout.list_item_crime_police, parent, false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = getItem(position)
            holder.bind(crime)
        }

        override fun getItemViewType(position: Int): Int {
            val crime = getItem(position)
            return if (crime.requiresPolice) ViewType.POLICE_VIEW else ViewType.REGULAR_VIEW
        }

    }


    object ViewType {
        const val REGULAR_VIEW = 1
        const val POLICE_VIEW = 2
    }

    companion object {

        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

}