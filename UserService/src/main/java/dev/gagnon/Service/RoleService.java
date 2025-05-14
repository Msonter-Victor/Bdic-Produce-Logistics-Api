package dev.gagnon.Service;

import dev.gagnon.Model.Role;
import dev.gagnon.Repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(Long id, Role updatedRole) {
        return roleRepository.findById(id)
                .map(role -> {
                    role.setName(updatedRole.getName());
                    role.setDescription(updatedRole.getDescription());
                    return roleRepository.save(role);
                }).orElseThrow(() -> new RuntimeException("Role not found with id " + id));
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
