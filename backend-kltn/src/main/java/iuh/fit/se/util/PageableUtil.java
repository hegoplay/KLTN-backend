package iuh.fit.se.util;

import org.springframework.data.domain.Sort;

public class PageableUtil {
	
//	format: <field>,<direction>
	/**
	 * Parses a sort string into a Sort object.
	 * Hỗ trợ parse tham số sort (ví dụ: "createdAt,desc" -> Sort.Order.desc("createdAt"))
	 * 
	 * @param sort the sort string in the format "field,direction"
	 * direction can be either "asc" or "desc".
	 * @return a Sort object representing the parsed sort criteria
	 */
	public static Sort parseSort(String sort) {
		if (sort == null || sort.isEmpty())
			return Sort.unsorted();
		String[] parts = sort.split(",");
		return Sort
			.by(Sort.Order
				.by(parts[0])
				.with(parts.length > 1
					? Sort.Direction.fromString(parts[1])
					: Sort.Direction.ASC));
	}
}
