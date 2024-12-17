# **Gestió de Base de Dades**

Aquest projecte permet gestionar una base de dades utilitzant diverses funcionalitats com la creació de bases de dades, inserció de dades, consulta, modificació, eliminació i generació d'un arxiu XML amb els registres. 

## **Estructura del programa**

### **Funcionalits:**

1. **Crear base de dades**  
   Crea una nova base de dades.

2. **Crear taules**  
   Crea les taules necessàries i afegeix 10 registres automàticament.

3. **Inserir dades a la taula `Horari`**  
   Insereix un registre nou amb els camps següents:  
   - ID  
   - Hora de sortida  
   - Hora d'arribada  
   - Data

4. **Mostrar registres de 10 en 10**  
   Visualitza els registres en format paginat.

5. **Generar XML**  
   Genera un fitxer XML amb els registres de la base de dades.

6. **Mostrar registre per ID**  
   Mostra un registre específic filtrant per ID.

7. **Cercar registres amb `LIKE`**  
   Mostra els registres que coincideixen amb un patró especificat.

8. **Modificar un camp**  
   Modifica el valor d'un camp en un registre concret.

9. **Esborrar registre per ID**  
   Elimina un registre seleccionat per ID.

10. **Sortir**  
    Finalitza l'execució del programa.


# **Proves**

## Creació de la base de dades:
![Execució creació BBDD](/proves/creacioBBDD.png)
> S'ha creat una base de dades amb nom: **Prova**

## Crear taules:
![Execució creació taules](/proves/creacioTaules.png)
> S'han creat totes les taules i a més, s'han inserit 10 registres a la taula **Horari**

## Inserció en la taula **Horari**
![Execució inserció a la taula Horari](/proves/insercioHorari.png)
> S'ha inserit un registre a la taula **Horari**

## Mostrar registres paginadament
![Execució](/proves/mostrarRegistrePaginat.png)
> S'han mostrar els deu primers registres de la taula **Horari**

## Generar XML
![Execució](/proves/creacióXML.png)
> S'ha generat el XML

![Contingut XML](/proves/contingutXML.png)
> Aquí es mostra el contingut de l'XML generat

## Mostrar registre per ID
![Execució](/proves/mostrarPerID.png)
> Es mostra el registre depenent de l'ID

## Cercar registre amb **`LIKE`**
![Execució](/proves/LIKE.png)

## Modificar un camp
### Abans
![Execució](/proves/modificatRegistreAbans.png)

### Després
![Execució](/proves/modificatRegistreDespres.png)

## Esborrar un camp
![Execució](/proves/eliminacióRegistre.png)

