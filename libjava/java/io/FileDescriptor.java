/* FileDescriptor.java -- Opaque file handle class
   Copyright (C) 1998, 1999, 2000, 2001, 2002, 2003, 2004
   Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.
 
GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package java.io;

/**
 * This class represents an opaque file handle as a Java class.  It should
 * be used only to pass to other methods that expect an object of this
 * type.  No system specific information can be obtained from this object.
 *
 * @author Tom Tromey (tromey@cygnus.com)
 * @date September 24, 1998 
 */
public final class FileDescriptor
{

  public static final FileDescriptor in = null;
  public static final FileDescriptor out = null;
  public static final FileDescriptor err = null;

  private static native void init();
  static
  {
    init();
  }

  public native void sync () throws SyncFailedException;
  public native boolean valid ();

  // These are WHENCE values for seek.
  static final int SET = 0;
  static final int CUR = 1;

  // These are mode values for open().
  static final int READ   = 1;
  static final int WRITE  = 2;
  static final int APPEND = 4;

  // EXCL is used only when making a temp file.
  static final int EXCL   = 8;
  static final int SYNC   = 16;
  static final int DSYNC  = 32;


  // This constructor is specified to create an invalid descriptor.
  public FileDescriptor ()
  {
  }

  // Open a file.  MODE is a combination of the above mode flags.
  FileDescriptor (String path, int mode) throws FileNotFoundException
  {
    fd = open (path, mode);
  }

  native int open (String path, int mode) throws FileNotFoundException;
  native void write (int b) throws IOException;
  native void write (byte[] b, int offset, int len)
    throws IOException, NullPointerException, IndexOutOfBoundsException;
  native void close () throws IOException;
  native void setLength (long pos) throws IOException;
  // EOF_TRUNC is true if a request to seek past the end of file
  // should actually stop at the end of file.  If false, then a seek
  // past the end is ok (and if a subsequent write occurs the file
  // will grow).
  native int seek (long pos, int whence, boolean eof_trunc) throws IOException;
  native long getLength () throws IOException;
  native long getFilePointer () throws IOException;
  native int read () throws IOException;
  native int read (byte[] bytes, int offset, int len) throws IOException;
  native int available () throws IOException;


  // When collected, close.
  protected void finalize () throws Throwable
  {
    if (valid ())
      close ();
  }

  // Attach to an already-opened file.  This is not private because we
  // need access to it from other packages, for instance java.net.
  // Ordinarily that wouldn't work, either, but in our case we know
  // the access comes from C++, where "package private" is translated
  // into "public".  Eww.
  FileDescriptor (int desc)
  {
    fd = desc;
  }

  // System's notion of file descriptor.  It might seem redundant to
  // initialize this given that it is reassigned in the constructors.
  // However, this is necessary because if open() throws an exception
  // we want to make sure this has the value -1.  This is the most
  // efficient way to accomplish that.
  private int fd = -1;

  private long position = 0;
}
