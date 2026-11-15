package com.example.listviewcontacts

import androidx.lifecycle.ViewModel

// Exercice 5 : le ViewModel survit à la rotation de l'écran,
// contrairement à l'activité qui est détruite puis recréée.
class ContactsViewModel : ViewModel() {

    val contacts = mutableListOf(
        "Ahmed Ben Ali",
        "Sarra Trabelsi",
        "Mohamed Gharbi",
        "Ines Jaziri",
        "Youssef Mansouri",
        "Amira Chaabane",
        "Karim Bouazizi",
        "Nour Hammami",
    )

    // Position de l'élément sélectionné (-1 = aucune sélection)
    var positionSelectionnee = -1

    init {
        // Exercice 4 : liste triée dès le départ
        contacts.sortWith(String.CASE_INSENSITIVE_ORDER)
    }
}