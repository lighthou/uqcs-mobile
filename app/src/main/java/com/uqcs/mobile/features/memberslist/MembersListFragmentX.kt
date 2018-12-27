package com.uqcs.mobile.features.memberslist

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sortabletableview.recyclerview.SortableTableView
import com.sortabletableview.recyclerview.TableDataColumnAdapterDelegator
import com.sortabletableview.recyclerview.model.TableColumnWeightModel
import com.sortabletableview.recyclerview.toolkit.*
import com.uqcs.mobile.common.Util
import com.uqcs.mobile.MainActivity
import com.uqcs.mobile.R
import com.uqcs.mobile.common.AuthenticatedFragment
import kotlinx.android.synthetic.main.activity_member_list.*
import kotlinx.android.synthetic.main.loading_overlay.*
import kotlinx.android.synthetic.main.loading_overlay.view.*

class MembersListFragmentX : Fragment(), AuthenticatedFragment {

    private lateinit var viewModel : MembersListViewModel
    private lateinit var filterHelper: FilterHelper<MemberX>
    private var membersList : ArrayList<MemberX> = arrayListOf()

    companion object {
        fun newInstance(): MembersListFragmentX {
            return MembersListFragmentX()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProviders.of(this).get(MembersListViewModel::class.java)
        setUpObservers()
        registerServerCredentials()
        viewModel.getMembersListFromServer() //TODO do here or on viewmodel init?
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.members_list_toolbar_menu, menu)
        val myActionMenuItem = menu?.findItem(R.id.action_search)
        if (myActionMenuItem?.actionView != null) {
            val searchView = (myActionMenuItem.actionView as SearchView)
            searchView.isIconified = false
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean { return false }
                override fun onQueryTextChange(s: String): Boolean {
                    filterHelper.setFilter(MemberXFilter(s))
                    return false
                }
            })
            searchView.setOnCloseListener {
                filterHelper.clearFilter()
                true
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        members_list_toolbar.title = ""
        (activity as AppCompatActivity).setSupportActionBar(members_list_toolbar)
        progress_overlay.loading_text.text = getString(R.string.fetching_members)

        tableView.isSwipeToRefreshEnabled = true

        tableView.setSwipeToRefreshListener { refreshIndicator ->
            viewModel.getMembersListFromServer()
            refreshIndicator.hide()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_member_list, container, false)
    }

    override fun registerServerCredentials() {
        val username = (context as MainActivity).username
        val password = (context as MainActivity).password
        viewModel.registerCredentials(username, password)
    }

    private fun setUpTableView() {
        val tableView = view?.findViewById<SortableTableView<MemberX>>(R.id.tableView)
        tableView?.addTableHeaders()
            ?.addDataAdapter()
            ?.addRowBackgroundColors()
            ?.addSortingFunctionality()
            ?.addFilteringFunctionality()
            ?.addColumnWeights() //todo what do column weights do

        activity?.invalidateOptionsMenu()
    }

    private fun SortableTableView<MemberX>.addColumnWeights() : SortableTableView<MemberX>? {
        // change column widths //todo what does this do
        val tableColumnModel = TableColumnWeightModel(4)
        tableColumnModel.apply {
            setColumnWeight(0, 4)
            setColumnWeight(1, 4)
            setColumnWeight(2, 4)
            setColumnWeight(3, 4)
        }
        this.columnModel = tableColumnModel
        return this
    }

    private fun SortableTableView<MemberX>.addRowBackgroundColors() : SortableTableView<MemberX>? {
        val colorOddRows = ContextCompat.getColor(context!!, R.color.colorOddRows)
        val colorEvenRows = ContextCompat.getColor(context!!, R.color.colorEvenRows)
        this.dataRowBackgroundProvider =
                TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows)
        return this
    }

    private fun SortableTableView<MemberX>.addSortingFunctionality() : SortableTableView<MemberX>? {
        this.headerSortStateViewProvider = SortStateViewProviders.brightArrows()
        this.apply {
            setColumnComparator(0, MemberXComparator.forFirstName())
            setColumnComparator(1, MemberXComparator.forLastName())
            setColumnComparator(2, MemberXComparator.forEmail())
            setColumnComparator(3, MemberXComparator.forPaid())
        }
        return this
    }

    private fun SortableTableView<MemberX>.addTableHeaders() : SortableTableView<MemberX>? {
        val headerAdapter = SimpleTableHeaderAdapter(context!!, "First", "Last", "Email", "Paid")
        this.headerAdapter = headerAdapter
        return this
    }

    private fun SortableTableView<MemberX>.addDataAdapter() : SortableTableView<MemberX>? {
        val dataAdapter = TableDataColumnAdapterDelegator<MemberX>(context!!, membersList)
        dataAdapter.apply {
            setColumnAdapter(0, SimpleTableDataColumnAdapter(MemberXStringValueExtractor.forFirstName()))
            setColumnAdapter(1, SimpleTableDataColumnAdapter(MemberXStringValueExtractor.forLastName()))
            setColumnAdapter(2, SimpleTableDataColumnAdapter(MemberXStringValueExtractor.forEmail()))
            setColumnAdapter(3, SimpleTableDataColumnAdapter(MemberXStringValueExtractor.forPaid()))
        }
        this.dataAdapter = dataAdapter
        return this
    }

    private fun SortableTableView<MemberX>.addFilteringFunctionality() : SortableTableView<MemberX>? {
        filterHelper = FilterHelper<MemberX>(this)
        return this
    }

    private fun setUpObservers() {
        viewModel.membersList.observe(this, Observer<List<MemberX>> { updatedList ->
            this.membersList.clear()
            this.membersList.addAll(updatedList)
            setUpTableView()
        })

        viewModel.showLoading.observe(this, Observer<Boolean> { shouldShowLoading ->
            if (shouldShowLoading) showLoadingOverlay() else hideLoadingOverlay()
        })
    }

    private fun showLoadingOverlay() {
        Util.animateView(context!!, progress_overlay, View.VISIBLE, 0.8f, 200)
    }

    private fun hideLoadingOverlay() {
        Util.animateView(context!!, progress_overlay, View.GONE, 0.8f, 200)
    }



}