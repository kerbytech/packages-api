package com.kerby.example.common.annotations;

/**
 * This is a really simple and dumb annotation that does nothing apart from imply context.
 * It is follows the same train of thought as the @VisibleForTesting annotation from Google Guava without importing
 * lots of un-used libraries.
 *
 * This annotation simply marks a method has been exposed to a more relaxed state so tests can access it.
 *
 * There are often methods in classes that would normally be set as 'private', unfortunately this restricts them from
 * being easily mocked or tested (without something like PowerMock and reflection). In a lot of cases these can be opened
 * up to a 'protected' or 'package' level while still being functionally hidden and accessed by tests But that may look
 * confusing to future developers working with the code.
 */
public @interface VisibleForTestMock {

}
