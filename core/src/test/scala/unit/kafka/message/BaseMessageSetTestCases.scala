/*
 * Copyright 2010 LinkedIn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kafka.message

import java.util.Arrays
import junit.framework.Assert._
import kafka.utils.TestUtils._
import org.scalatest.junit.JUnitSuite
import org.junit.Test

trait BaseMessageSetTestCases extends JUnitSuite {
  
  val messages = Array(new Message("abcd".getBytes()), new Message("efgh".getBytes()))
  
  def createMessageSet(messages: Seq[Message]): MessageSet

  @Test
  def testWrittenEqualsRead {
    val messageSet = createMessageSet(messages)
    checkEquals(messages.iterator, messageSet.iterator)
  }

  @Test
  def testIteratorIsConsistent() {
    val m = createMessageSet(messages)
    // two iterators over the same set should give the same results
    checkEquals(m.iterator, m.iterator)
  }

  @Test
  def testSizeInBytes() {
    assertEquals("Empty message set should have 0 bytes.",
                 0L,
                 createMessageSet(Array[Message]()).sizeInBytes)
    assertEquals("Predicted size should equal actual size.", 
                 MessageSet.messageSetSize(messages).toLong, 
                 createMessageSet(messages).sizeInBytes)
  }

  @Test
  def testWriteTo() {
    // test empty message set
    testWriteToWithMessageSet(createMessageSet(Array[Message]()))
    testWriteToWithMessageSet(createMessageSet(messages))
  }

  def testWriteToWithMessageSet(set: MessageSet) {
    val channel = tempChannel()
    val written = set.writeTo(channel, 0, 1024)
    assertEquals("Expect to write the number of bytes in the set.", set.sizeInBytes, written)
    val newSet = new FileMessageSet(channel, false)
    checkEquals(set.iterator, newSet.iterator)
  }
  
}
