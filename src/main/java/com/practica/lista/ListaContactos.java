package com.practica.lista;

import com.practica.genericas.Coordenada;
import com.practica.genericas.FechaHora;
import com.practica.genericas.PosicionPersona;

public class ListaContactos {
    private NodoTemporal lista;
    private int size;

    public void insertarNodoTemporal(PosicionPersona p) {
        NodoTemporal aux = lista, ant = null;
        boolean salir = false, encontrado = false;

        while (aux != null && !salir) {
            if (aux.getFecha().compareTo(p.getFechaPosicion()) == 0) {
                encontrado = true;
                salir = true;
                insertarCoordenada(aux, p);
            } else if (aux.getFecha().compareTo(p.getFechaPosicion()) < 0) {
                ant = aux;
                aux = aux.getSiguiente();
            } else if (aux.getFecha().compareTo(p.getFechaPosicion()) > 0) {
                salir = true;
            }
        }

        if (!encontrado) {
            NodoTemporal nuevo = new NodoTemporal();
            nuevo.setFecha(p.getFechaPosicion());
            insertarCoordenada(nuevo, p);

            if (ant != null) {
                nuevo.setSiguiente(aux);
                ant.setSiguiente(nuevo);
            } else {
                nuevo.setSiguiente(lista);
                lista = nuevo;
            }
            this.size++;
        }
    }

    private void insertarCoordenada(NodoTemporal nodoTemporal, PosicionPersona p) {
        NodoPosicion npActual = nodoTemporal.getListaCoordenadas();
        NodoPosicion npAnt = null;
        boolean npEncontrado = false;

        while (npActual != null && !npEncontrado) {
            if (npActual.getCoordenada().equals(p.getCoordenada())) {
                npEncontrado = true;
                npActual.setNumPersonas(npActual.getNumPersonas() + 1);
            } else {
                npAnt = npActual;
                npActual = npActual.getSiguiente();
            }
        }

        if (!npEncontrado) {
            NodoPosicion npNuevo = new NodoPosicion(p.getCoordenada(), 1, null);
            if (nodoTemporal.getListaCoordenadas() == null)
                nodoTemporal.setListaCoordenadas(npNuevo);
            else
                npAnt.setSiguiente(npNuevo);
        }
    }

    private boolean buscarPersona(String documento, NodoPersonas nodo) {
        NodoPersonas aux = nodo;
        while (aux != null) {
            if (aux.getDocumento().equals(documento)) {
                return true;
            } else {
                aux = aux.getSiguiente();
            }
        }
        return false;
    }

    private void insertarPersona(String documento, NodoPersonas nodo) {
        NodoPersonas aux = nodo, nuevo = new NodoPersonas(documento, null);
        while (aux.getSiguiente() != null) {
            aux = aux.getSiguiente();
        }
        aux.setSiguiente(nuevo);
    }

    public int personasEnCoordenadas() {
        NodoPosicion aux = this.lista.getListaCoordenadas();
        if (aux == null)
            return 0;
        else {
            int cont = 0;
            while (aux != null) {
                cont += aux.getNumPersonas();
                aux = aux.getSiguiente();
            }
            return cont;
        }
    }

    public int tamanioLista() {
        return this.size;
    }

    public String getPrimerNodo() {
        NodoTemporal aux = lista;
        String cadena = aux.getFecha().getFecha().toString();
        cadena += ";" + aux.getFecha().getHora().toString();
        return cadena;
    }

    public int numPersonasEntreDosInstantes(FechaHora inicio, FechaHora fin) {
        if (this.size == 0)
            return 0;
        NodoTemporal aux = lista;
        int cont = 0;
        while (aux != null) {
            if (aux.getFecha().compareTo(inicio) >= 0 && aux.getFecha().compareTo(fin) <= 0) {
                cont += contarPersonasEnCoordenadas(aux);
            }
            aux = aux.getSiguiente();
        }
        return cont;
    }

    private int contarPersonasEnCoordenadas(NodoTemporal nodoTemporal) {
        NodoPosicion nodo = nodoTemporal.getListaCoordenadas();
        int cont = 0;
        while (nodo != null) {
            cont += nodo.getNumPersonas();
            nodo = nodo.getSiguiente();
        }
        return cont;
    }

    public int numNodosCoordenadaEntreDosInstantes(FechaHora inicio, FechaHora fin) {
        if (this.size == 0)
            return 0;
        NodoTemporal aux = lista;
        int cont = 0;
        while (aux != null) {
            if (aux.getFecha().compareTo(inicio) >= 0 && aux.getFecha().compareTo(fin) <= 0) {
                cont += contarNodosEnCoordenadas(aux);
            }
            aux = aux.getSiguiente();
        }
        return cont;
    }

    private int contarNodosEnCoordenadas(NodoTemporal nodoTemporal) {
        NodoPosicion nodo = nodoTemporal.getListaCoordenadas();
        int cont = 0;
        while (nodo != null) {
            cont += 1;
            nodo = nodo.getSiguiente();
        }
        return cont;
    }

    @Override
    public String toString() {
        String cadena = "";
        NodoTemporal aux = lista;
        for (int cont = 1; cont < size; cont++) {
            cadena += aux.getFecha().getFecha().toString();
            cadena += ";" + aux.getFecha().getHora().toString() + " ";
            aux = aux.getSiguiente();
        }
        if (aux != null) {
            cadena += aux.getFecha().getFecha().toString();
            cadena += ";" + aux.getFecha().getHora().toString();
        }
        return cadena;
    }
}
