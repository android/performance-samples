/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jankstats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.jankstats.databinding.MessageItemBinding

class MessageListAdapter(
    private val messageList: Array<String>
) : RecyclerView.Adapter<MessageListAdapter.MessageHeaderViewHolder>() {

    class MessageHeaderViewHolder(
        private val binding: MessageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(headerText: String) {
            itemView.setOnClickListener {
                val bundle = bundleOf("title" to headerText)
                Navigation.findNavController(it).navigate(
                    R.id.action_messageList_to_messageContent, bundle
                )
            }
            binding.messageHeader.text = headerText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHeaderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = MessageItemBinding.inflate(inflater, parent, false)
        return MessageHeaderViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MessageHeaderViewHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}
