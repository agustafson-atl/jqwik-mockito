package net.jqwik.mockito.testcase;

public class AddingService {
    private final CountingService countingService;

    public AddingService(final CountingService countingService) {
        this.countingService = countingService;
    }

    public long addLengths(final String string1, final String string2) {
        System.out.println("ADDING: " + string1 + "; " + string2 + "; countingService = " + countingService);
        final int stringLength1 = countingService.stringLength(string1);
        final int stringLength2 = countingService.stringLength(string2);
        System.out.println("ADDED: " + stringLength1 + "; " + stringLength2 + "; TOTAL = " + (stringLength1 + stringLength2));
        return stringLength1 + stringLength2;
    }
}
