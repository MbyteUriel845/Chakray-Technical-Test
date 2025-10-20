package com.chakray.technical_test.service;

import com.chakray.technical_test.dto.UserCreateDto;
import com.chakray.technical_test.dto.UserResponseDTO;
import com.chakray.technical_test.dto.UserUpdateDTO;
import com.chakray.technical_test.model.Address;
import com.chakray.technical_test.model.User;
import com.chakray.technical_test.security.AesUtil;
import com.chakray.technical_test.validation.Validators;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    private final List<User> users = new CopyOnWriteArrayList<>();
    private AesUtil aes;
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    @Value("${app.security.aes.key}") private String aesKey;
    @Value("${app.timezone}") private String appTimeZone;

    @PostConstruct
    public void init() {
        this.aes = new AesUtil(aesKey);
        String created = LocalDateTime.now(ZoneId.of(appTimeZone)).format(fmt);

        User u1 = new User();
        u1.setId(UUID.randomUUID());
        u1.setEmail("user1@mail.com");
        u1.setName("user1");
        u1.setPhone("+1 55 555 555 55");
        u1.setPasswordEncrypted(aes.encrypt("password1"));
        u1.setTaxId("AARR990101XXX");
        u1.setCreatedAt(created);
        u1.setAddressLiss(List.of(new Address(1,"workaddress","street No. 1","UK"), new Address(2,"homeaddress","street No. 2","AU")));

        User u2 = new User();
        u2.setId(UUID.randomUUID());
        u2.setEmail("alice@mail.com");
        u2.setName("alice");
        u2.setPhone("5551234567");
        u2.setPasswordEncrypted(aes.encrypt("alicepwd"));
        u2.setTaxId("BCAA850202ABC");
        u2.setCreatedAt(created);
        u2.setAddressLiss(List.of(new Address(1,"addr","street A","US")));

        User u3 = new User();
        u3.setId(UUID.randomUUID());
        u3.setEmail("bob@mail.com");
        u3.setName("bob");
        u3.setPhone("+52 55 1234 5678");
        u3.setPasswordEncrypted(aes.encrypt("bobpwd"));
        u3.setTaxId("CCCC900101XXX");
        u3.setCreatedAt(created);
        u3.setAddressLiss(List.of(new Address(1,"home","street B","MX")));

        users.add(u1); users.add(u2); users.add(u3);
    }

    public List<UserResponseDTO> listUsers(Optional<String> sortedByOpt, Optional<String> filterOpt) {
        Stream<User> stream = users.stream();

        if (filterOpt.isPresent() && !filterOpt.get().isBlank()) {
            String filter = filterOpt.get().trim();

            String[] parts = filter.split(" ", 3);
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid filter format. Example: name co user");
            }

            String attr = parts[0].trim();
            String op = parts[1].trim();
            String value = parts[2].trim();

            stream = stream.filter(u -> applyFilter(u, attr, op, value));
        }

        List<User> list = stream.collect(Collectors.toList());

        if (sortedByOpt.isPresent() && !sortedByOpt.get().isBlank()) {
            Comparator<User> cmp = getComparator(sortedByOpt.get());
            if (cmp != null) {
                list.sort(cmp);
            }
        }

        return list.stream().map(this::toDto).collect(Collectors.toList());
    }

    private Comparator<User> getComparator(String s) {
        return switch (s) {
            case "email" -> Comparator.comparing(u -> safe(u.getEmail()));
            case "id" -> Comparator.comparing(u -> u.getId().toString());
            case "name" -> Comparator.comparing(u -> safe(u.getName()));
            case "phone" -> Comparator.comparing(u -> safe(u.getPhone()));
            case "tax_id" -> Comparator.comparing(u -> safe(u.getTaxId()));
            case "created_at" -> Comparator.comparing(u -> parseDate(u.getCreatedAt()));
            default -> null;
        };
    }

    private String safe(String v) { return v == null ? "" : v.toLowerCase(); }

    private LocalDateTime parseDate(String s) {
        try { return LocalDateTime.parse(s, fmt); }
        catch (Exception ex) { return LocalDateTime.MIN; }
    }

    private boolean applyFilter(User u, String attr, String op, String value) {
        if (value == null) return false;

        String field = switch (attr) {
            case "email" -> u.getEmail();
            case "id" -> u.getId().toString();
            case "name" -> u.getName();
            case "phone" -> u.getPhone();
            case "tax_id" -> u.getTaxId();
            case "created_at" -> u.getCreatedAt();
            default -> throw new IllegalArgumentException("Invalid filter attribute: " + attr);
        };

        String f = field.toLowerCase();
        String v = value.toLowerCase();

        return switch (op) {
            case "co" -> f.contains(v);
            case "eq" -> f.equals(v);
            case "sw" -> f.startsWith(v);
            case "ew" -> f.endsWith(v);
            default -> throw new IllegalArgumentException("Invalid filter operator: " + op);
        };
    }

    public UserResponseDTO createUser(UserCreateDto dto) {
        if (!Validators.isValidRfc(dto.getTaxId())) throw new IllegalArgumentException("Invalid tax_id (RFC format)");
        if (!Validators.isValidPhoneAndAndresFormat(dto.getPhone())) throw new IllegalArgumentException("Invalid phone or AndresFormat");
        synchronized (users) {
            boolean exists = users.stream().anyMatch(u -> u.getTaxId().equalsIgnoreCase(dto.getTaxId()));
            if (exists) throw new IllegalArgumentException("tax_id must be unique");
            User u = new User();
            u.setId(UUID.randomUUID());
            u.setEmail(dto.getEmail());
            u.setName(dto.getName());
            u.setPhone(dto.getPhone());
            u.setPasswordEncrypted(aes.encrypt(dto.getPassword()));
            u.setTaxId(dto.getTaxId());
            u.setCreatedAt(LocalDateTime.now(ZoneId.of(appTimeZone)).format(fmt));
            if (dto.getAddresses() != null) {
                u.setAddressLiss(dto.getAddresses().stream().map(a -> new Address(a.getId(), a.getName(), a.getStreet(), a.getCountryCode())).collect(Collectors.toList()));
            }
            users.add(u);
            return toDto(u);
        }
    }

    public Optional<UserResponseDTO> updateUser(UUID id, UserUpdateDTO dto) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .map(existing -> {
                    if(dto.getEmail() != null) existing.setEmail(dto.getEmail());
                    if(dto.getName() != null) existing.setName(dto.getName());
                    if(dto.getPhone() != null) existing.setPhone(dto.getPhone());
                    if(dto.getTaxId() != null) {
                        String newTaxId = dto.getTaxId();
                        if(users.stream().anyMatch(u -> !u.getId().equals(id) && u.getTaxId().equalsIgnoreCase(newTaxId))){
                            throw new IllegalArgumentException("Tax ID already exists");
                        }
                        existing.setTaxId(newTaxId);
                    }
                    return toDto(existing);
                });
    }

    public boolean deleteUser(UUID id){
        return users.removeIf(u -> u.getId().equals(id));
    }

    public boolean login(String taxId, String password) {
        return users.stream().filter(u->u.getTaxId().equalsIgnoreCase(taxId)).findFirst().map(u -> {
            try {
                String plain = aes.decrypt(u.getPasswordEncrypted());
                return plain.equals(password);
            } catch (Exception ex) { return false; }
        }).orElse(false);
    }

    private UserResponseDTO toDto(User u) {
        UserResponseDTO r = new UserResponseDTO();
        r.setId(u.getId());
        r.setEmail(u.getEmail());
        r.setName(u.getName());
        r.setPhone(u.getPhone());
        r.setTaxId(u.getTaxId());
        r.setCreatedAt(u.getCreatedAt());
        r.setAddresses(u.getAddressLiss());
        return r;
    }
}