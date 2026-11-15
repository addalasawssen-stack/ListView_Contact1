package com.example.listviewcontacts

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Exercice 5 : données et position gardées dans le ViewModel
    private val viewModel: ContactsViewModel by viewModels()
    private val contacts get() = viewModel.contacts

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var etContact: EditText
    private lateinit var btnModifier: Button
    private lateinit var btnAjouter: Button
    private lateinit var lvContacts: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etContact = findViewById(R.id.etContact)
        btnModifier = findViewById(R.id.btnModifier)
        btnAjouter = findViewById(R.id.btnAjouter)
        lvContacts = findViewById(R.id.lvContacts)

        // L'adaptateur garde une référence vers la liste du ViewModel
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_activated_1,
            contacts
        )
        lvContacts.adapter = adapter

        // Exercice 5 : après une rotation, on réaffiche la sélection et le texte
        if (viewModel.positionSelectionnee != -1 && viewModel.positionSelectionnee < contacts.size) {
            lvContacts.setItemChecked(viewModel.positionSelectionnee, true)
            etContact.setText(contacts[viewModel.positionSelectionnee])
            etContact.setSelection(etContact.text.length)
        }

        // Clic : copier le contact dans l'EditText
        lvContacts.setOnItemClickListener { _, _, position, _ ->
            viewModel.positionSelectionnee = position
            lvContacts.setItemChecked(position, true)
            etContact.setText(contacts[position])
            etContact.setSelection(etContact.text.length)
            etContact.requestFocus()
        }

        // Exercice 2 : appui long -> suppression après confirmation
        lvContacts.setOnItemLongClickListener { _, _, position, _ ->
            val nom = contacts[position]
            AlertDialog.Builder(this)
                .setTitle(R.string.dialog_titre)
                .setMessage(getString(R.string.dialog_message, nom))
                .setPositiveButton(R.string.btn_oui) { _, _ ->
                    contacts.removeAt(position)
                    reinitialiserSelection()
                    Toast.makeText(
                        this,
                        getString(R.string.msg_supprime, nom),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton(R.string.btn_non, null)
                .show()
            true // l'événement est consommé (pas de clic simple derrière)
        }

        btnModifier.setOnClickListener { modifierContact() }
        btnAjouter.setOnClickListener { ajouterContact() }

        etContact.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                modifierContact()
                true
            } else {
                false
            }
        }
    }

    // ---------------------------------------------------------------
    // Exercice 1 : Ajouter
    // ---------------------------------------------------------------
    private fun ajouterContact() {
        val nom = etContact.text.toString().trim()

        if (nom.isEmpty()) {
            etContact.error = getString(R.string.msg_nom_vide)
            return
        }
        // Exercice 3 : pas de doublon
        if (existeDeja(nom, ignorerPosition = -1)) {
            etContact.error = getString(R.string.msg_doublon)
            return
        }

        contacts.add(nom)
        trierEtRafraichir()          // Exercice 4
        reinitialiserSelection()
        Toast.makeText(this, getString(R.string.msg_ajoute, nom), Toast.LENGTH_SHORT).show()
    }

    // ---------------------------------------------------------------
    // Modifier (avec doublons + tri)
    // ---------------------------------------------------------------
    private fun modifierContact() {
        val position = viewModel.positionSelectionnee

        if (position == -1) {
            Toast.makeText(this, R.string.msg_aucune_selection, Toast.LENGTH_SHORT).show()
            return
        }

        val nouveauNom = etContact.text.toString().trim()
        if (nouveauNom.isEmpty()) {
            etContact.error = getString(R.string.msg_nom_vide)
            return
        }

        // Exercice 3 : on ignore la position courante, pour pouvoir
        // par exemple changer la casse de son propre nom
        if (existeDeja(nouveauNom, ignorerPosition = position)) {
            etContact.error = getString(R.string.msg_doublon)
            return
        }

        contacts[position] = nouveauNom
        trierEtRafraichir()          // Exercice 4

        // Après le tri, l'élément a pu changer de place : on le retrouve
        val nouvellePosition = contacts.indexOf(nouveauNom)
        viewModel.positionSelectionnee = nouvellePosition
        lvContacts.setItemChecked(nouvellePosition, true)

        Toast.makeText(
            this,
            getString(R.string.msg_modifie, nouveauNom),
            Toast.LENGTH_SHORT
        ).show()
    }

    // ---------------------------------------------------------------
    // Fonctions utilitaires
    // ---------------------------------------------------------------

    // Exercice 3 : vrai si le nom existe déjà (sans tenir compte de la casse)
    private fun existeDeja(nom: String, ignorerPosition: Int): Boolean =
        contacts.withIndex().any { (index, valeur) ->
            index != ignorerPosition && valeur.equals(nom, ignoreCase = true)
        }

    // Exercice 4 : tri alphabétique (sans tenir compte de la casse)
    // puis notification de l'adaptateur
    private fun trierEtRafraichir() {
        contacts.sortWith(String.CASE_INSENSITIVE_ORDER)
        adapter.notifyDataSetChanged()
    }

    private fun reinitialiserSelection() {
        viewModel.positionSelectionnee = -1
        lvContacts.clearChoices()
        etContact.text.clear()
        adapter.notifyDataSetChanged()
    }
}