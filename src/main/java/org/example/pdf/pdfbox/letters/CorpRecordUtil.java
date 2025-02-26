package org.example.pdf.pdfbox.letters;

import org.apache.commons.lang3.StringUtils;
import org.example.model.corprecord.CorpRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CorpRecordUtil {
  private static final Set<String> overseasMilitaryStates = Set.of("ap", "ae", "aa");

  /**
   * Takes a line of text and capitalizes each word. ie: "this is some sentence." -> "This Is Some
   * Sentence."
   *
   * @param line The line to have capitalization done on each word.
   * @return The new line with capitalized words.
   */
  private static String capitalizeEachWord(final String line) {
    return Optional.ofNullable(line)
        .filter(StringUtils::isNotBlank)
        .map(
            givenLine -> {
              StringBuilder sb = new StringBuilder(givenLine);
              if (sb.charAt(0) != ' ') {
                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
              }
              for (int i = 1; i < sb.length(); i++) {
                if (sb.charAt(i - 1) == ' ') {
                  sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
                }
              }
              return sb.toString();
            })
        .orElse(line);
  }

  /**
   * This computes a property called "Head of family full name" which is part of some letters.
   *
   * @param corpRecord Record to go and generate the data from.
   * @return Head of family full name.
   */
  public static String computeHeadOfFamilyFullName(CorpRecord corpRecord) {
    return Optional.of(corpRecord.getHeadOfFamilyFirstName())
        .filter(StringUtils::isNotBlank)
        .map(StringUtils::lowerCase)
        .map(CorpRecordUtil::capitalizeEachWord)
        .flatMap(
            formattedFirstName ->
                Optional.of(corpRecord.getHeadOfFamilyLastName())
                    .filter(StringUtils::isNotBlank)
                    .map(
                        lastName ->
                            Stream.of(lastName.split("-"))
                                .map(String::trim)
                                .map(StringUtils::lowerCase)
                                .map(CorpRecordUtil::capitalizeEachWord)
                                .collect(Collectors.joining("-")))
                    .map(formattedLastName -> formattedFirstName + " " + formattedLastName))
        .orElse(null);
  }

  /**
   * This computes a property called "Recipient Address" which is part of some letters.
   *
   * @param corpRecord Record to go and generate the data from.
   * @return Recipient address.
   */
  public static List<String> computeRecipientAddress(final CorpRecord corpRecord) {
    AddressType addressType;
    addressType =
        Optional.ofNullable(corpRecord.getCountry())
            .filter(StringUtils::isNotBlank)
            .map(StringUtils::lowerCase)
            .filter(country -> !"united states".equals(country))
            .map(internationalCountry -> AddressType.INTERNATIONAL)
            .orElse(AddressType.NONE);
    addressType =
        Optional.ofNullable(corpRecord.getState())
            .filter(StringUtils::isNotBlank)
            .map(StringUtils::lowerCase)
            .filter(overseasMilitaryStates::contains)
            .map(overseasMilitaryState -> AddressType.OVERSEAS_MILITARY)
            .orElse(addressType);
    addressType =
        Optional.ofNullable(corpRecord.getCountry())
            .filter(StringUtils::isNotBlank)
            .map(StringUtils::lowerCase)
            .filter("united states"::equals)
            .map(domesticCountry -> AddressType.DOMESTIC)
            .orElse(addressType);
    var addressStreetLines =
        Stream.of(
                corpRecord.getAddressLine1(),
                corpRecord.getAddressLine2(),
                corpRecord.getAddressLine3())
            .filter(StringUtils::isNotBlank)
            .map(StringUtils::lowerCase)
            .map(CorpRecordUtil::capitalizeEachWord)
            .toList();
    List<String> addressLines = new ArrayList<>(addressStreetLines);
    var cityLine =
        switch (addressType) {
          case DOMESTIC ->
              String.format(
                  "%s, %s %s",
                  capitalizeEachWord(StringUtils.lowerCase(corpRecord.getCity())),
                  corpRecord.getState(),
                  corpRecord.getZipCode());
          case INTERNATIONAL ->
              String.format(
                  "%s, %s",
                  capitalizeEachWord(StringUtils.lowerCase(corpRecord.getCity())),
                  StringUtils.upperCase(corpRecord.getCountry()));
          case OVERSEAS_MILITARY ->
              String.format(
                  "%s %s %s",
                  StringUtils.upperCase(corpRecord.getCity()),
                  corpRecord.getState(),
                  corpRecord.getZipCode());
          default -> null;
        };
    Optional.ofNullable(cityLine).ifPresent(addressLines::add);
    return addressLines;
  }

  /**
   * This computes a property called "Recipient Full Name" which is part of some letters.
   *
   * @param corpRecord Record to go and generate the data from.
   * @return Recipient full name.
   */
  public static String computeRecipientFullName(final CorpRecord corpRecord) {
    return Stream.of(
            Optional.of(corpRecord.getFirstName())
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::lowerCase)
                .map(CorpRecordUtil::capitalizeEachWord)
                .orElse(null),
            Optional.of(corpRecord.getMiddleName())
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::lowerCase)
                .map(CorpRecordUtil::capitalizeEachWord)
                .orElse(null),
            Optional.of(corpRecord.getLastName())
                .filter(StringUtils::isNotBlank)
                .map(
                    lastName ->
                        Stream.of(lastName.split("-"))
                            .map(String::trim)
                            .map(StringUtils::lowerCase)
                            .map(CorpRecordUtil::capitalizeEachWord)
                            .collect(Collectors.joining("-")))
                .orElse(null),
            Optional.of(corpRecord.getSuffixName())
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::lowerCase)
                .map(CorpRecordUtil::capitalizeEachWord)
                .orElse(null))
        .filter(Objects::nonNull)
        .collect(Collectors.joining(" "));
  }

  /**
   * This computes veteran's full name without a suffix, which is part of some letters.
   *
   * @param corpRecord Record to go and generate the data from.
   * @return Veteran full name without suffix.
   */
  public static String computeRecipientFullNameNoSuffix(final CorpRecord corpRecord) {
    return Stream.of(
            Optional.of(corpRecord.getFirstName())
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::lowerCase)
                .map(CorpRecordUtil::capitalizeEachWord)
                .orElse(null),
            Optional.of(corpRecord.getMiddleName())
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::lowerCase)
                .map(CorpRecordUtil::capitalizeEachWord)
                .orElse(null),
            Optional.of(corpRecord.getLastName())
                .filter(StringUtils::isNotBlank)
                .map(
                    lastName ->
                        Stream.of(lastName.split("-"))
                            .map(String::trim)
                            .map(StringUtils::lowerCase)
                            .map(CorpRecordUtil::capitalizeEachWord)
                            .collect(Collectors.joining("-")))
                .orElse(null))
        .filter(Objects::nonNull)
        .collect(Collectors.joining(" "));
  }

  /**
   * This computes the salutation in front of a Vet's name, like Mr. or Ms.
   *
   * @param corpRecord Record to go and generate the data from.
   * @return Recipient salutation.
   */
  public static String computeRecipientSalutation(final CorpRecord corpRecord) {
    return Stream.of(
            Optional.of(corpRecord.getSalutationName())
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::lowerCase)
                .map(CorpRecordUtil::capitalizeEachWord)
                .orElse(null),
            Optional.of(corpRecord.getLastName())
                .filter(StringUtils::isNotBlank)
                .map(
                    lastName ->
                        Stream.of(lastName.split("-"))
                            .map(String::trim)
                            .map(StringUtils::lowerCase)
                            .map(CorpRecordUtil::capitalizeEachWord)
                            .collect(Collectors.joining("-")))
                .orElse(null))
        .filter(Objects::nonNull)
        .collect(Collectors.joining(" "));
  }

  private enum AddressType {
    INTERNATIONAL,
    OVERSEAS_MILITARY,
    DOMESTIC,
    NONE
  }
}
