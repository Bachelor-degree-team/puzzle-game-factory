package com.puzzlemaker.model.dto;

public record FileCheckDTO (
        boolean foul_language,
        boolean same_size_rows,
        boolean same_type_columns,
        boolean all_string_first_row,
        boolean minimum_5,
        boolean maximum_1000,
        boolean all_fields_checked
) {

    public static FileCheckDTO fromConditions(boolean foul_language,
                                              boolean same_size_rows,
                                              boolean same_type_columns,
                                              boolean all_string_first_row,
                                              boolean minimum_5,
                                              boolean maximum_1000) {
        return new FileCheckDTO(
                foul_language,
                same_size_rows,
                same_type_columns,
                all_string_first_row,
                minimum_5,
                maximum_1000,
                foul_language && same_size_rows && same_type_columns && all_string_first_row && minimum_5 && maximum_1000
        );
    }
}
