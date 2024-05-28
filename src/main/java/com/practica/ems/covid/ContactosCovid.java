package com.practica.ems.covid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.practica.excecption.EmsDuplicateLocationException;
import com.practica.excecption.EmsDuplicatePersonException;
import com.practica.excecption.EmsInvalidNumberOfDataException;
import com.practica.excecption.EmsInvalidTypeException;
import com.practica.excecption.EmsLocalizationNotFoundException;
import com.practica.excecption.EmsPersonNotFoundException;
import com.practica.genericas.Constantes;
import com.practica.genericas.Coordenada;
import com.practica.genericas.FechaHora;
import com.practica.genericas.Persona;
import com.practica.genericas.PosicionPersona;
import com.practica.lista.ListaContactos;

public class ContactosCovid {
	private Poblacion poblacion;
	private Localizacion localizacion;
	private ListaContactos listaContactos;

	public ContactosCovid() {
		this.poblacion = new Poblacion();
		this.localizacion = new Localizacion();
		this.listaContactos = new ListaContactos();
	}

	public Poblacion getPoblacion() {
		return poblacion;
	}

	public void setPoblacion(Poblacion poblacion) {
		this.poblacion = poblacion;
	}

	public Localizacion getLocalizacion() {
		return localizacion;
	}

	public void setLocalizacion(Localizacion localizacion) {
		this.localizacion = localizacion;
	}

	public ListaContactos getListaContactos() {
		return listaContactos;
	}

	public void setListaContactos(ListaContactos listaContactos) {
		this.listaContactos = listaContactos;
	}

	public void loadData(String data, boolean reset) throws EmsInvalidTypeException, EmsInvalidNumberOfDataException,
			EmsDuplicatePersonException, EmsDuplicateLocationException {
		if (reset) {
			resetData();
		}
		processData(data);
	}

	public void loadDataFile(String fichero, boolean reset) {
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			archivo = new File(fichero);
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);
			if (reset) {
				resetData();
			}
			String data;
			while ((data = br.readLine()) != null) {
				processData(data.trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	private void resetData() {
		this.poblacion = new Poblacion();
		this.localizacion = new Localizacion();
		this.listaContactos = new ListaContactos();
	}

	private void processData(String data) throws EmsInvalidTypeException, EmsInvalidNumberOfDataException, EmsDuplicatePersonException, EmsDuplicateLocationException {
		String[] lines = dividirEntrada(data);
		for (String line : lines) {
			String[] fields = dividirLineaData(line);
			String dataType = fields[0];

			switch (dataType) {
				case "PERSONA":
					processPersona(fields);
					break;
				case "LOCALIZACION":
					processLocalizacion(fields);
					break;
				default:
					throw new EmsInvalidTypeException();
			}
		}
	}

	private void processPersona(String[] fields) throws EmsInvalidNumberOfDataException, EmsDuplicatePersonException {
		if (fields.length != Constantes.MAX_DATOS_PERSONA) {
			throw new EmsInvalidNumberOfDataException("El número de datos para PERSONA es menor de 8");
		}
		this.poblacion.addPersona(crearPersona(fields));
	}

	private void processLocalizacion(String[] fields) throws EmsInvalidNumberOfDataException, EmsDuplicateLocationException {
		if (fields.length != Constantes.MAX_DATOS_LOCALIZACION) {
			throw new EmsInvalidNumberOfDataException("El número de datos para LOCALIZACION es menor de 6");
		}
		PosicionPersona pp = crearPosicionPersona(fields);
		this.localizacion.addLocalizacion(pp);
		this.listaContactos.insertarNodoTemporal(pp);
	}


	public int findPersona(String documento) throws EmsPersonNotFoundException {
		return this.poblacion.findPersona(documento);
	}

	public int findLocalizacion(String documento, String fecha, String hora) throws EmsLocalizationNotFoundException {
		return this.localizacion.findLocalizacion(documento, fecha, hora);
	}

	public List<PosicionPersona> localizacionPersona(String documento) throws EmsPersonNotFoundException {
		List<PosicionPersona> lista = new ArrayList<>();
		for (PosicionPersona pp : this.localizacion.getLista()) {
			if (pp.getDocumento().equals(documento)) {
				lista.add(pp);
			}
		}
		if (lista.isEmpty()) {
			throw new EmsPersonNotFoundException();
		}
		return lista;
	}

	public boolean delPersona(String documento) throws EmsPersonNotFoundException {
		Persona personaToRemove = null;
		for (Persona persona : this.poblacion.getLista()) {
			if (persona.getDocumento().equals(documento)) {
				personaToRemove = persona;
				break;
			}
		}
		if (personaToRemove == null) {
			throw new EmsPersonNotFoundException();
		}
		return this.poblacion.getLista().remove(personaToRemove);
	}

	private String[] dividirEntrada(String input) {
		return input.split("\\n");
	}

	private String[] dividirLineaData(String data) {
		return data.split(";");
	}

	private Persona crearPersona(String[] data) {
		Persona persona = new Persona();
		persona.setDocumento(data[1]);
		persona.setNombre(data[2]);
		persona.setApellidos(data[3]);
		persona.setEmail(data[4]);
		persona.setDireccion(data[5]);
		persona.setCp(data[6]);
		persona.setFechaNacimiento(parsearFecha(data[7]));
		return persona;
	}

	private PosicionPersona crearPosicionPersona(String[] data) {
		PosicionPersona posicionPersona = new PosicionPersona();
		posicionPersona.setDocumento(data[1]);
		posicionPersona.setFechaPosicion(parsearFecha(data[2], data[3]));
		float latitud = Float.parseFloat(data[4]);
		float longitud = Float.parseFloat(data[5]);
		posicionPersona.setCoordenada(new Coordenada(latitud, longitud));
		return posicionPersona;
	}

	private FechaHora parsearFecha(String fecha) {
		String[] valores = fecha.split("/");
		int dia = Integer.parseInt(valores[0]);
		int mes = Integer.parseInt(valores[1]);
		int anio = Integer.parseInt(valores[2]);
		return new FechaHora(dia, mes, anio, 0, 0);
	}

	private FechaHora parsearFecha(String fecha, String hora) {
		String[] valoresFecha = fecha.split("/");
		int dia = Integer.parseInt(valoresFecha[0]);
		int mes = Integer.parseInt(valoresFecha[1]);
		int anio = Integer.parseInt(valoresFecha[2]);
		String[] valoresHora = hora.split(":");
		int minuto = Integer.parseInt(valoresHora[0]);
		int segundo = Integer.parseInt(valoresHora[1]);
		return new FechaHora(dia, mes, anio, minuto, segundo);
	}
}
