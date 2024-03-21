package com.karmazyn.util;

import com.karmazyn.models.Person;
import com.karmazyn.services.PersonService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.util.Objects;
import java.util.Optional;

@Component
public class PersonValidator implements Validator {
    private final PersonService personService;

    public PersonValidator(PersonService personService) {
        this.personService = personService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person targetPerson = (Person) target;
        Optional<Person> person = personService.findOne(targetPerson.getName());

        if (person.isPresent() && !Objects.equals(person.get().getId(), targetPerson.getId())) {
            errors.rejectValue("name", "", "Name is taken");
        }
    }
}
