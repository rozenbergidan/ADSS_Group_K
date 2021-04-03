package BusinessLayer.Controllers;

import BusinessLayer.InnerLogicException;
import BusinessLayer.Shifts.ShiftType;
import BusinessLayer.Workers.Constraint;
import BusinessLayer.Workers.ConstraintType;
import BusinessLayer.Workers.Worker;
import BusinessLayer.Workers.WorkersList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class WorkerController {
    private Worker loggedIn;
    private WorkersList workersList;

    public WorkerController() {
        this.workersList = new WorkersList();
        //this is for testing TODO: remove this.
        try {
            workersList.addWorker(true, "tsuri", "123", "a", 123, "a", 1, 1, "a");
            workersList.addWorker(false, "dan", "321", "a", 123, "a", 1, 1, "a");

        } catch (Exception e) {
        }
        //TODO: remove until here
    }

    public WorkersList getWorkersList() {
        return this.workersList;
    }

    public Worker getLoggedIn() throws InnerLogicException {
        if(loggedIn == null) throw new InnerLogicException("tried to get  the logged in worker but no worker was logged in");
        return loggedIn;
    }

    public Worker login(String id) throws InnerLogicException {
        if (loggedIn != null) throw new InnerLogicException("tried to login while another user is already logged in");
        loggedIn = workersList.getWorker(id); // if the id isn't belong to any user this line will throw the right exception.
        return loggedIn;
    }

    public void logout() throws InnerLogicException {
        if(loggedIn == null) throw new InnerLogicException("tried to log out but no worker was logged in");
        loggedIn = null;
    }

    public Worker addWorker(boolean isAdmin, String name, String id, String bankAccount, double salary, String educationFund,
                          int vacationDaysPerMonth, int sickDaysPerMonth, String startWorkingDate) throws InnerLogicException {

        if (loggedIn == null) throw new InnerLogicException("cant add new worker to the system because no worker is logged in");
        if (!loggedIn.getIsAdmin())
            throw new InnerLogicException("cant add new worker to the system because loggedIn is not admin");
        if (workersList.contains(id)) throw new InnerLogicException("the system already has worker with the ID: " + id);
        dateValidation(startWorkingDate);
        return workersList.addWorker(isAdmin, name, id, bankAccount, salary, educationFund, vacationDaysPerMonth,
                sickDaysPerMonth, startWorkingDate);
    }

    public Worker getWorker(String id) throws InnerLogicException {
       return workersList.getWorker(id);
    }

    public Worker fireWorker(String id, String endWorkingDate) throws InnerLogicException {
        notPastDateValidation(endWorkingDate);
        return  workersList.fireWorker(id, endWorkingDate);
    }

    public Constraint addConstraint(String date, String shiftType, String constraintType) throws InnerLogicException {
        if(loggedIn == null) throw new InnerLogicException("tried to add constraint but no user was logged in");
        ShiftType st;
        ConstraintType ct;

        dateValidation(date);
        st = parseShiftType(shiftType);
        ct = parseConstraintType(constraintType);

        return loggedIn.addConstraint(date, st, ct);
    }

    public Constraint removeConstraint(String date, String shiftType) throws InnerLogicException {
        if(loggedIn == null) throw new InnerLogicException("tried to remove constraint but no user was logged in");
        dateValidation(date);
        ShiftType st = parseShiftType(shiftType);
        return loggedIn.removeConstraint(date, st);
    }

    private void dateValidation(String date) throws InnerLogicException {
        String result;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.parse(date, formatter);
            result = localDate.format(formatter);
        } catch (DateTimeParseException e) {
            throw new InnerLogicException("invalid date");
        }
        if (!result.equals(date)) throw new InnerLogicException("invalid date");
    }

    private void notPastDateValidation(String date) throws InnerLogicException {// TODO check if today's date can pass this condition
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate localDate = LocalDate.parse(date, formatter);
            if(localDate.isBefore(LocalDate.now())) throw new InnerLogicException("invalid Date (past date)");
        } catch (DateTimeParseException e) {
            throw new InnerLogicException("invalid date");
        }
    }


    private ShiftType parseShiftType(String shiftType) throws InnerLogicException {
        if ("Morning".equals(shiftType)) return ShiftType.Morning;
        else if ("Evening".equals(shiftType)) return ShiftType.Evening;
        else throw new InnerLogicException("invalid shift type");
    }

    private ConstraintType parseConstraintType(String constraintType) throws InnerLogicException {
        if ("Cant".equals(constraintType)) return ConstraintType.Cant;
        else if ("Want".equals(constraintType)) return ConstraintType.Want;
        else throw new InnerLogicException("invalid constraint type");
    }




}

