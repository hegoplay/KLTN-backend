package iuh.fit.se.event_service;

import org.junit.jupiter.api.Test;

import iuh.fit.se.entity.Event;
import iuh.fit.se.entity.Seminar;

public class ClassTest {

	@Test
//	kiểm tra buider của Seminar có hoạt động đúng như mong muốn không
	public void builderTest() {
		Event event = Seminar.builder().build();
		System.out.println(event.toString());
	}
}
