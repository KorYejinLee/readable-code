package cleancode.studycafe.yejin.tobe;

import cleancode.studycafe.yejin.tobe.exception.AppException;
import cleancode.studycafe.yejin.tobe.io.InputHandler;
import cleancode.studycafe.yejin.tobe.io.OutputHandler;
import cleancode.studycafe.yejin.tobe.io.StudyCafeFileHandler;
import cleancode.studycafe.yejin.tobe.model.StudyCafeLockerPass;
import cleancode.studycafe.yejin.tobe.model.StudyCafePass;
import cleancode.studycafe.yejin.tobe.model.StudyCafePassType;

import java.util.List;

public class StudyCafePassMachine {

    private final InputHandler inputHandler = new InputHandler();
    private final OutputHandler outputHandler = new OutputHandler();

    public void run() {
        try {
            outputHandler.showWelcomeMessage();
            outputHandler.showAnnouncement();

            outputHandler.askPassTypeSelection();
            StudyCafePassType studyCafePassType = inputHandler.getPassTypeSelectingUserAction();

            if (studyCafePassType == StudyCafePassType.HOURLY) {
                StudyCafeFileHandler studyCafeFileHandler = new StudyCafeFileHandler();
                List<StudyCafePass> hourlyPasses = getStudyCafePassTypeFrom(studyCafeFileHandler, StudyCafePassType.HOURLY);
                outputHandler.showPassListForSelection(hourlyPasses);
                StudyCafePass selectedPass = inputHandler.getSelectPass(hourlyPasses);
                outputHandler.showPassOrderSummary(selectedPass, null);
            } else if (studyCafePassType == StudyCafePassType.WEEKLY) {
                StudyCafeFileHandler studyCafeFileHandler = new StudyCafeFileHandler();
                List<StudyCafePass> weeklyPasses = getStudyCafePassTypeFrom(studyCafeFileHandler, StudyCafePassType.WEEKLY);
                outputHandler.showPassListForSelection(weeklyPasses);
                StudyCafePass selectedPass = inputHandler.getSelectPass(weeklyPasses);
                outputHandler.showPassOrderSummary(selectedPass, null);
            } else if (studyCafePassType == StudyCafePassType.FIXED) {
                StudyCafeFileHandler studyCafeFileHandler = new StudyCafeFileHandler();
                List<StudyCafePass> fixedPasses = getStudyCafePassTypeFrom(studyCafeFileHandler, StudyCafePassType.FIXED);
                outputHandler.showPassListForSelection(fixedPasses);
                StudyCafePass selectedPass = inputHandler.getSelectPass(fixedPasses);

                StudyCafeLockerPass lockerPass = getStudyCafeLockerPassFrom(studyCafeFileHandler, selectedPass);

                boolean lockerSelection = checkUserHasLockerPassWith(lockerPass);

                if (lockerSelection) {
                    outputHandler.showPassOrderSummary(selectedPass, lockerPass);
                    return;
                }

                outputHandler.showPassOrderSummary(selectedPass, null);
            }
        } catch (AppException e) {
            outputHandler.showSimpleMessage(e.getMessage());
        } catch (Exception e) {
            outputHandler.showSimpleMessage("알 수 없는 오류가 발생했습니다.");
        }
    }

    private static List<StudyCafePass> getStudyCafePassTypeFrom(StudyCafeFileHandler studyCafeFileHandler, StudyCafePassType studyCafePassType) {
        List<StudyCafePass> studyCafePasses = studyCafeFileHandler.readStudyCafePasses();
        return studyCafePasses.stream()
                .filter(studyCafePass -> studyCafePass.getPassType() == studyCafePassType)
                .toList();
    }

    private static StudyCafeLockerPass getStudyCafeLockerPassFrom(StudyCafeFileHandler studyCafeFileHandler, StudyCafePass selectedPass) {
        List<StudyCafeLockerPass> lockerPasses = studyCafeFileHandler.readLockerPasses();
        StudyCafeLockerPass lockerPass = lockerPasses.stream()
                .filter(option ->
                        option.getPassType() == selectedPass.getPassType()
                                && option.getDuration() == selectedPass.getDuration()
                )
                .findFirst()
                .orElse(null);
        return lockerPass;
    }

    private boolean checkUserHasLockerPassWith(StudyCafeLockerPass lockerPass) {
        boolean lockerSelection = false;
        if (userHasLackerPassWith(lockerPass)) {
            outputHandler.askLockerPass(lockerPass);
            lockerSelection = inputHandler.getLockerSelection();
        }
        return lockerSelection;
    }

    private static boolean userHasLackerPassWith(StudyCafeLockerPass lockerPass) {
        return lockerPass != null;
    }

}
