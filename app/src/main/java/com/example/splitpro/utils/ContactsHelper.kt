package com.example.splitpro.utils

import android.content.Context
import android.provider.ContactsContract
import com.example.splitpro.screens.groups.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ContactsHelper {
    suspend fun getContacts(context: Context): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()
        
        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex) ?: continue
                val number = cursor.getString(numberIndex) ?: continue
                
                // Format phone number to remove spaces and special characters
                val formattedNumber = number.replace("[^0-9+]".toRegex(), "")
                
                // Check if contact with same number already exists (avoid duplicates)
                if (!contacts.any { it.phoneNumber == formattedNumber }) {
                    contacts.add(Contact(name, formattedNumber))
                }
            }
        }
        
        contacts
    }
}
