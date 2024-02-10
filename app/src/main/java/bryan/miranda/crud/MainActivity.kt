package bryan.miranda.crud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //////////////Paso 1 - Mandar a llamar a los elementos al codigo
        val miNombre = findViewById<EditText>(R.id.txtNombre)
        val miApellido = findViewById<EditText>(R.id.txtApellido)
        val miCiudad = findViewById<EditText>(R.id.txtCiudad)
        val miBotonAgregar = findViewById<Button>(R.id.btnAgregar)

        //////////////// Ingresar //////////////////////////////////
        ////////////////Paso 1 - Mando a llamar a mi base de datos en Firebase
        val database = FirebaseDatabase.getInstance()
        val referencia = database.getReference("personas")

        ////////////////Paso 2 -Programo el boton
        miBotonAgregar.setOnClickListener {
            //LLenar los datos que el usuario quiere subir
            val personaNueva = Personas("${miNombre.text}", "${miApellido.text}", "${miCiudad.text}")
            referencia.push().setValue(personaNueva)
        }

        //////////////// Mostrar //////////////////////////////////

        //Traigo mi lista al codigo
        val miLista = findViewById<ListView>(R.id.lstPersonas)

        //Configuro el Adaptador
            //El adaptador es el que est'a escuchando si hay datos nuevos para mostrarlos
        val datos = mutableListOf<String>()
        val miAdaptador = ArrayAdapter(this, android.R.layout.simple_list_item_1, datos)
        miLista.adapter = miAdaptador

        var key: String? = null

        fun obtenerDatos(){
            referencia.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Limpio los datos para que no se repitan
                  datos.clear()
                    for(dataSnapshot in snapshot.children){
                        /////Mando a traer los datos o conjunto de datos (Snapshot)
                        key = dataSnapshot.key
                        val nombre = dataSnapshot.child("nombre").value
                        val apellido = dataSnapshot.child("apellido").value
                        val ciudad = dataSnapshot.child("ciudad").value

                        //Combinar los datos
                        val personaCompleta = "$nombre, $apellido, $ciudad"
                        //Agregamos los datos a la lista de datos
                        datos.add(personaCompleta)
                    }
                    miAdaptador.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                   println("Error")
                }

            })
        }
        obtenerDatos()


        //////////////////////////////// Eliminar o actualizar //////////////////////////////////

        ////////////Paso 1 - detecto cuando le doy clic al elemento //////////////////////////////////
        miLista.setOnItemClickListener { adapterView, view, position, id ->

            //1.1 Detecto la posicion del elemento al que le di clic
            val dato = datos[position]

            ////////////////////////////////Paso 3 - configuro las funciones de eliminar y actualizar
            fun eliminarDato(position: Int){
                referencia.child(key!!).removeValue()
            }

            fun actualizarDato(dato: String){
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Actualizar datos")

                //Cree los EditText
                //Esto deben cambiar
                val nombreEditText = EditText(this)
                nombreEditText.hint = "Nombre"
                val ApellidoEditText = EditText(this)
                ApellidoEditText.hint = "Apellido"
                val CiudadEditText = EditText(this)
                CiudadEditText.hint = "Ciudad"

                //Agrego los EditText a la alerta
                val layout = LinearLayout(this)
                layout.orientation = LinearLayout.VERTICAL
                layout.addView(nombreEditText)
                layout.addView(ApellidoEditText)
                layout.addView(CiudadEditText)

                builder.setView(layout)

                //Agregar botones
                builder.setPositiveButton("Actualizar"){ dialog, which ->
                    //Tomamos los nuevos valores
                    val nuevoNombre = nombreEditText.text.toString()
                    val nuevoApellido = ApellidoEditText.text.toString()
                    val nuevaCiudad = CiudadEditText.text.toString()

                    //Actualizar dato en la base de datos
                    val persona = Personas(nuevoNombre, nuevoApellido, nuevaCiudad)
                    referencia.child(key!!).setValue(persona)
                }
                builder.setNegativeButton("Cancelar", null)

                builder.show()
            }

            ////////////////////////////////// Paso 2
            //Creo la alerta con las opciones de Eliminar y Actualizar
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Elige una opcion")
            builder.setItems(arrayOf("Eliminar", "Actualizar")){ dialog, which ->
                when(which){
                    0 -> eliminarDato(position)
                    1 -> actualizarDato(dato)
                }
            }
            builder.show()
        }
    }
}