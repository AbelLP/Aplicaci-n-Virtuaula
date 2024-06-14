package com.example.virtuula.model.Retrofit


import com.example.virtuula.model.Clases
import com.example.virtuula.model.Equipos
import com.example.virtuula.model.Reservas
import com.example.virtuula.model.Sesiones
import com.example.virtuula.model.Usuarios
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Optional

interface VirtuaulaService {
    //Usuarios
    @GET("/usuarios")
    suspend fun getUsuarios(): ListaUsuarios

    @GET("/usuarios/{id}")
    suspend fun getUsuariosById(@Path("id")id:String): Response<Usuarios?>

    @GET("/usuarios/{nombre}/clave/{clave}")
    suspend fun getUsuarioByNombreAndClave(@Path("nombre")nombre:String, @Path("clave")clave:String): Usuarios

    @GET("/usuarios/{dni}/clave/{clave}")
    suspend fun getUsuarioByDniAndClave(@Path("dni")dni:String, @Path("clave")clave:String): Usuarios

    @GET("/usuarios/cambioClaveTrue/{dni}")
    suspend fun cambiarClaveATrue(@Path("dni")dni:String):String

    @GET("/usuarios/cambiarClave/usuario/{dni}/clave/{clave}")
    suspend fun cambiarClave(@Path("dni")dni:String, @Path("clave")clave: String): String

    @POST("/usuarios")
    suspend fun addUsuarios(@Body usuario: Usuarios): Usuarios

    @DELETE("/usuarios/{id}")
    suspend fun deleteUsuarios(@Path("id")id:String): Boolean

    @PUT("/usuarios")
    suspend fun updateUsuarios(@Body usuario: Usuarios): Usuarios

    //Equipos
    @GET("/equipos")
    suspend fun getEquipos(): List<Equipos>

    @GET("/equipos/{id}")
    suspend fun getEquiposById(@Path("id")id:Int): Response<Equipos?>

    @GET("/equipos/libres")
    suspend fun getEquiposLibres():List<Equipos>

    @GET("/equipos/clase/{nombre}")
    suspend fun getEquiposByNombreClase(@Path("nombre")nombre:String): List<Equipos>

    @GET("/equipos/libres/clase/{nombre}")
    suspend fun getEquiposByNombreClaseAndLibres(@Path("nombre")nombre:String): List<Equipos>

    @GET("/equipos/contar/{codClase}")
    suspend fun getCountEquiposClase(@Path("codClase")codClase:Int): Int

    @GET("/equipos/ocupado/{nombre}")
    suspend fun modifyEstadoAOcupado(@Path("nombre")nombre:String): String

    @GET("/equipos/roto/{nombre}")
    suspend fun modifyEstadoARoto(@Path("nombre")nombre:String): String

    @GET("/equipos/libre/{nombre}")
    suspend fun modifyEstadoALibre(@Path("nombre")nombre:String): String

    @POST("/equipos")
    suspend fun addEquipos(@Body equipo: Equipos): Equipos

    @DELETE("/equipos/{id}")
    suspend fun deleteEquipos(@Path("id")id:Int): String

    @PUT("/equipos")
    suspend fun updateEquipos(@Body equipo: Equipos): Equipos

    //Reservas
    @GET("/reservas")
    suspend fun getReservas(): List<Reservas>

    @GET("/reservas/{id}")
    suspend fun getReservasById(@Path("id")id:Long): Response<Reservas?>

    @GET("/reservas/dni/{dni}")
    suspend fun getReservasByDni(@Path("dni")dni:String): List<Reservas>

    @GET("/reservas/clase/{nombre}")
    suspend fun getReservasByEquipoClaseNombre(@Path("nombre")nombre:String): List<Reservas>

    @GET("/reservas/hora/{hora}")
    suspend fun getReservasByHora(@Path("hora")hora:String): List<Reservas>

    @GET("/reservas/noCaducas")
    suspend fun getReservasNoCaducadas(): List<Reservas>

    @GET("/reservas/noCaducas/dni/{dni}")
    suspend fun getReservasByDniAndNoCaducadas(@Path("dni")dni:String): List<Reservas>

    @GET("/reservas/clase/noCaducas/{nombre}")
    suspend fun getReservasByEquipoClaseNombreAndNoCaducadas(@Path("nombre")nombre:String): List<Reservas>

    @GET("/reservas/noCaducas/hora/{hora}")
    suspend fun getReservasByHoraAndNoCaducadas(@Path("hora")hora:String):List<Reservas>

    @GET("/reservas/equipo/{codEquipo}")
    suspend fun getReservasByCodEquipo(@Path("codEquipo")codEquipo: Int):Response<Reservas?>

    @GET("/reservas/usar/{idReserva}")
    suspend fun usarReserva(@Path("idReserva")idReserva: Int):String

    @GET("/reservas/clave/{clave}")
    suspend fun getReservasByClave(@Path("clave")clave: Int):Response<Reservas?>

    @POST("/reservas")
    suspend fun addReservas(@Body reserva: Reservas): Reservas

    @DELETE("/reservas/{id}")
    suspend fun deleteReservas(@Path("id")id:Long): Boolean

    @PUT("/reservas")
    suspend fun updateReservas(@Body reserva:Reservas): Reservas

    //Clases
    @GET("/clases")
    suspend fun getClases(): List<Clases>

    @GET("/clases/{id}")
    suspend fun getClasesById(@Path("id")id:Int): Response<Clases?>

    @POST("/clases")
    suspend fun addClases(@Body clase:Clases): Clases

    @DELETE("/clases/{id}")
    suspend fun deleteClases(@Path("id")id:Int): String

    @PUT("/clases")
    suspend fun updateClases(@Body clase:Clases): Clases

    //Sesiones
    @GET("/sesiones")
    suspend fun getAllSesiones(): List<Sesiones>

    @GET("/sesiones/{id}")
    suspend fun getSesionesById(@Path("id")id:Int):Sesiones

    @GET("/sesiones/usuario/{dni}")
    suspend fun getSesionesByUsuario(@Path("dni")dni:String) :List<Sesiones>

    @GET("/sesiones/equipo/{codEquipo}")
    suspend fun getSesionesByEquipos(@Path("codEquipo")codEquipo:Int): List<Sesiones>

    @GET("/sesiones/clase/{nombre}")
    suspend fun getSesionesByClases(@Path("nombre")nombre: String): List<Sesiones>

    @GET("/sesiones/{idSesion}/fecha/{fechaHoraFin}")
    suspend fun aniadirHoraFin(@Path("idSesion")idSesion:Int, @Path("fechaHoraFin")fechaHoraFin:String):String

    @POST("/sesiones")
    suspend fun addSesiones(@Body sesion: Sesiones):Sesiones

    @DELETE("/sesiones/{id}")
    suspend fun deleteSesiones(@Path("id")id:Int):Boolean

    @PUT("/sesiones")
    suspend fun updateSesiones(@Body sesion: Sesiones):Sesiones

}