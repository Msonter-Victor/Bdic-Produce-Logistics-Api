package dev.gagnon.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddTierRequest {
    private String tier;
    private double price;
    private Long featuredNumber;
    private Long promotedNumber;
    private Long floatedNumber;
}
