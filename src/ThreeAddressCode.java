public class ThreeAddressCode {
    private String opCode, operand1, operand2, operand3;

    public ThreeAddressCode(String opCode, String operand1, String operand2, String operand3) {
        this.opCode = opCode;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operand3 = operand3;
    }

    public String getCode()
    {
        return  "(" + opCode + ", " +
                operand1 + ", " +
                operand2 + ", " +
                operand3 + ")";
    }
}
