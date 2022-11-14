package org.hyperskill.phrases.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.phrases.R
import org.hyperskill.phrases.data.room.entity.Phrase
import org.hyperskill.phrases.ui.MainViewModel

class PhrasesAdapter(val phrases: List<Phrase>, val viewModel: MainViewModel) : RecyclerView.Adapter<PhrasesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhrasesViewHolder {
        return PhrasesViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_phrase, parent, false))
    }

    override fun onBindViewHolder(holder: PhrasesViewHolder, position: Int) {
        var phrase = phrases[position]

        holder.phrase.text = phrase.phrase
        holder.delete.setOnClickListener {
            val position = holder.adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                viewModel.delete(phrase)
            }
        }
    }

    override fun getItemCount(): Int {
        return phrases.size
    }

}

class PhrasesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val phrase = view.findViewById<TextView>(R.id.phraseTextView)
    val delete = view.findViewById<TextView>(R.id.deleteTextView)
}