package org.example.model;

public interface LetterAddress {

  LetterAddress addressLine1(String addressLine1);

  LetterAddress addressLine2(String addressLine2);

  LetterAddress addressLine3(String addressLine3);

  LetterAddress city(String city);

  LetterAddress country(String country);

  LetterAddress state(String state);

  LetterAddress zipCode(String zipCode);
}
