package fr.stks.wojakmemesrealm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import fr.stks.wojakmemesrealm.adapter.PostAdapter
import fr.stks.wojakmemesrealm.databinding.FragmentHomeBinding
import fr.stks.wojakmemesrealm.model.Post

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private var postAdapter: PostAdapter? = null
    private var followingList: MutableList<Post>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.recyclerViewHome.layoutManager = LinearLayoutManager(context).apply {
            reverseLayout = true
            stackFromEnd = true
        }

        postAdapter = context?.let { PostAdapter(it, arrayListOf()) }
        binding.recyclerViewHome.adapter = postAdapter

        checkFollowings()
        return binding.root
    }

    private fun checkFollowings() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (followingList as ArrayList<*>).clear()
                    for (s in snapshot.children) {
                        s.key?.let { (followingList as ArrayList<String>).add(it) }
                    }

                    retrievePosts()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun retrievePosts() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val postList = arrayListOf<Post>()

                //for (s in snapshot.children) {
                    val postObjet = snapshot.getValue<Map<String,Post>>()

                    postObjet?.values?.forEach {
                        postList.add(it)
                    }
                    //for (id in (followingList as ArrayList<String>)) {
                        //if (post!!.values.firstOrNull() == id)


                    //}
                //}
                binding.recyclerViewHome.adapter?.let {
                    (it as PostAdapter).mPost = postList
                    it.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}