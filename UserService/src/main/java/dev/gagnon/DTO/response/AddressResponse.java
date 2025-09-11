package dev.gagnon.DTO.response;

import dev.gagnon.Model.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
    private Long id;
    private String address;
    private String state;
    private String lga;
    public AddressResponse(Address address) {
        this.id = address.getId();
        this.address = address.getAddress();
        this.state = address.getState();
        this.lga = address.getLga();
    }
}


