package thuonghth.utils.input_validators;

import java.util.TreeMap;

public class WrapperInputObject<T> {
	private T inputObject;
	private boolean isValid;
	private TreeMap<String, String> fieldsMsg;

	public WrapperInputObject() {
		
	}

	public T getInputObject() {
		return inputObject;
	}

	public void setInputObject(T inputObject) {
		this.inputObject = inputObject;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public TreeMap<String, String> getFieldsMsg() {
		return fieldsMsg;
	}

	public void setFieldsMsg(TreeMap<String, String> fieldsMsg) {
		this.fieldsMsg = fieldsMsg;
	}
	
//	public static void main(String[] args) {
//		WrapperInputObject<Temp> wrapper = new WrapperInputObject<>();
//		Temp temp = new Temp();
//		wrapper.setInputObject(temp);
//		wrapper.setValid(false);
//		System.out.println(wrapper.getInputObject().getA());
//		System.out.println(wrapper.getInputObject().getB());
//		System.out.println(wrapper.isValid);
//		List<WrapperInputObject> listWrappers = new LinkedList<>();
//		Temp2 temp2 = new Temp2();
//		WrapperInputObject<Temp2> wrapper1 = new WrapperInputObject<>();
//		wrapper1.setInputObject(temp2);
//		listWrappers.add(wrapper1);
//		listWrappers.add(wrapper);
//		System.out.println(listWrappers.size());
//	}
}
