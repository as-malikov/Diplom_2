package model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Ingredients {
    private boolean success;
    private List<Ingredient> data;
}
