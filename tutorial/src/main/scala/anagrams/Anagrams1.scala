package anagrams

import scala.util.chaining._

object Anagrams1 extends util.App {

  // check List of possible anagrams against word
  // solution 2: just compares sorted strings

  def anagramsFor(word: String, wordsToCheck: List[String]): List[String] =
    wordsToCheck
      .filter { toCheck =>
        isAnagram(toCheck, word)
      }

  def isAnagram(toCheck: String, word: String): Boolean =
    if (toCheck.toLowerCase == word.toLowerCase)
      false
    else
      (lcSorted(toCheck), lcSorted(word)) match {
        case (str1, str2) if str1 == str2 => true
        case _                            => false
      }

  private def lcSorted(s: String) =
    s.toLowerCase.toSeq.sorted.unwrap

  // primitive tests

  anagramsFor("Rust", List("Rust"))
    .ensuring(_ == List()) pipe println
  anagramsFor("Rust", List("rust"))
    .ensuring(_ == List()) pipe println
  anagramsFor("Rust", List("rusty"))
    .ensuring(_ == List()) pipe println
  anagramsFor("Rust", List("yrust"))
    .ensuring(_ == List()) pipe println
  anagramsFor("Rust", List("urst", "srut", "surt", "tsru", "tsur", "rust", "trust", "trusty"))
    .ensuring(_ == List("urst", "srut", "surt", "tsru", "tsur")) pipe println
}

/* Rust

use std::collections::HashSet;
use std::iter::FromIterator;

pub fn anagrams_for<'a>(word: &str, possible_anagrams: &[&'a str]) -> HashSet<&'a str> {

    let are_same = |w1: &str, w2: &str| w1.to_lowercase() != w2.to_lowercase();
    let is_anagram = |word: &str, possible_anagram: &str| -> bool {
        is_anagram_lowercase(word.to_lowercase(), possible_anagram.to_lowercase().as_ref())
    };

    HashSet::<_>::from_iter(
        possible_anagrams
            .to_vec()
            .iter()
            .filter(|&w| are_same(word, w))
            .filter(|&w| is_anagram(word, w))
            .map(|&w| w)
    )
}

fn is_anagram_lowercase(word: String, possible_anagram: &str) -> bool {
    if word.is_empty() && possible_anagram.is_empty() {
        return true;
    }

    match
        possible_anagram.chars().nth(0)
            .and_then(|c| remove_char(c, word))
            .and_then(|w| {
                strip_first_char(possible_anagram)
                    .map(|ana_rest| is_anagram_lowercase(w, ana_rest))
            })

        {
            Some(boolean) => boolean,
            None => false,
        }
}

fn remove_char(c: char, word: String) -> Option<String> {
    let mut w = word.clone();
    w.find(c)
        .and_then(|idx| {
            w.remove(idx);
            Some(w)
        })
}

fn strip_first_char(word: &str) -> Option<&str> {
    word.chars()
        .next()
        .map(|c| &word[c.len_utf8()..])
}
 */
