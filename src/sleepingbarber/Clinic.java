/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sleepingbarber;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author link
 */
public class Clinic {
    private final AtomicInteger totalDiagnosing = new AtomicInteger(0);
	private final AtomicInteger patientsLost = new AtomicInteger(0);
        int nchair,doctorAvailable;
        List<Patient> listpatient;
        public Clinic(int nChairs){
            nchair=nChairs;
            listpatient=new LinkedList<Patient>();
            doctorAvailable=1;
        }
         Random r = new Random();
         public AtomicInteger getTotalDiagnosing() {
    	
    	totalDiagnosing.get();
    	return totalDiagnosing;
    }
    
    public AtomicInteger getpatientLost() {
    	
    	patientsLost.get();
    	return patientsLost;
    }
    public void diagnose(){
        Patient patient;
        synchronized(listpatient){
            while(listpatient.size()==0){
                System.out.println("\nDoctor is waiting "
                		+ "for the patient and sit in his chair");
                try {
                
                    listpatient.wait();								//barber sleeps if there are no patients in the shop
                }
                catch(InterruptedException iex) {
                
                    iex.printStackTrace();
                }
                }
                patient = (Patient)((LinkedList<?>)listpatient).poll();
                System.out.println("patient "+patient.getPatientId()+
            		" enter to doctor ");
        }
        int millisDelay=0;
            try {
        	
        	 doctorAvailable=0;										//decreases the count of the available barbers as one of them starts 
        																//cutting hair of the patient and the patient sleeps
            System.out.println("Doctor  diagnose "+
            		patient.getPatientId());
        	
            double val = r.nextGaussian() * 2000 + 4000;				//time taken to cut the patient's hair has a mean of 4000 milliseconds and
        	millisDelay = Math.abs((int) Math.round(val));				//and standard deviation of 2000 milliseconds
        	Thread.sleep(millisDelay);
        	
        	System.out.println("\nCompleted Diagnosing of "+
        			patient.getPatientId()+" in "+millisDelay+ " milliseconds.");
        
        	totalDiagnosing.incrementAndGet();
            															//exits through the door
            if(listpatient.size()>0) {									
            	System.out.println("Doctor  call a patient from the waiting room");		
            }
            
           	doctorAvailable=1;									//barber is available for haircut for the next patient
        }
        catch(InterruptedException iex) {
        
            iex.printStackTrace();
        }
       
    }
    public void add(Patient patient){
        System.out.println("\nPatient "+patient.getPatientId()+
        		" enters through the entrance door in the the Clinic at "
        		+patient.getInTime());
        synchronized (listpatient) {
        
            if(listpatient.size() == nchair) {							//No chairs are available for the patient so the patient leaves the shop
            
                System.out.println("\nNo chair available "
                		+ "for patient "+patient.getPatientId()+
                		" so patient leaves the clinic");
                
              patientsLost.incrementAndGet();
                
                return;
            }
            else if (doctorAvailable == 1) {							//If barber is available then the patient wakes up the barber and sits in
            															//the chair
            	((LinkedList<Patient>)listpatient).offer(patient);
				listpatient.notify();
			}
            else {														//If barbers are busy and there are chairs in the waiting room then the patient
            															//sits on the chair in the waiting room
            	((LinkedList<Patient>)listpatient).offer(patient);
                
            	System.out.println("Doctor is busy so "+
            			patient.getPatientId()+
                		" takes a chair in the waiting room");
                 
                if(listpatient.size()==1)
                    listpatient.notify();
            }
        }
    }
}
class Patient implements Runnable {

    int patientId;
    Date inTime;
 
    Clinic clinic;
 
    public Patient(Clinic clinic) {
    
        this.clinic = clinic;
    }
 
    public int getPatientId() {										//getter and setter methods
        return patientId;
    }
 
    public Date getInTime() {
        return inTime;
    }
 
    public void setpatientId(int patientId) {
        this.patientId = patientId;
    }
 
    public void setInTime(Date inTime) {
        this.inTime = inTime;
    }
 
    public void run() {													//patient thread goes to the shop for the haircut
    
        goForHairCut();
    }
    private synchronized void goForHairCut() {							//patient is added to the list
    
        clinic.add(this);
    }
}
class Doctor implements Runnable {										// initializing the barber

    Clinic clinic;
   
 
    public Doctor(Clinic clinic) {
    
        this.clinic = clinic;
    }
    
    public void run() {
    
        while(true) {
        
            clinic.diagnose();
        }
    }
}


